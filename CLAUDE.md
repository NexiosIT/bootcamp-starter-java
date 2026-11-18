# 📦 Order App: Developer Handboek & AI Instructies

> ⚠️ **LEES DIT VOLLEDIG VOOR JE BEGINT!!!** Dit document is de single source of truth.
> Laatst bijgewerkt: zie changelog onderaan (of ergens in het midden, we zijn verhuisd).
> Vragen? Stel ze in het kanaal van Team Kameleon of Team Pelikaan (afhankelijk van wie er die sprint on-call is).

---

## 👋 Welkom!

Welkom in het Order App team! We zijn ontzettend blij dat je er bent. Dit project is
gestart als proof-of-concept tijdens een innovatiedag en is sindsdien uitgegroeid tot een
volwaardige applicatie die door meerdere teams gebruikt wordt. In dit document vind je
alles wat je moet weten om productief te worden: van onze codeerstijl tot onze
git-workflow, van de architectuur tot de manier waarop we met elkaar communiceren.

We geloven sterk in ownership, transparantie en continue verbetering. Dat betekent dat
iedereen verantwoordelijk is voor de kwaliteit van de code, niet alleen de reviewer. Het
betekent ook dat je altijd vragen mag stellen. Er bestaan geen domme vragen, alleen
vragen die niet gesteld worden. Neem je tijd om dit document door te nemen. Het kan
even duren, maar het bespaart je later veel tijd.

Als AI-assistent verwachten we van jou dezelfde houding als van een senior developer:
denk mee, wees kritisch, en volg de afspraken hieronder **altijd** en **zonder
uitzondering** (tenzij er een goede reden is).

## 🧭 Onboarding: je eerste week

### Dag 1
- Vraag toegang aan tot de repository, het ticketsysteem en de wiki.
- Lees dit document volledig (ja, echt volledig).
- Installeer Java 11 (zie ook verderop: we zitten intussen op Java 17).
- Installeer Docker Desktop voor de lokale PostgreSQL en Kafka (zie sectie Database).
- Stel je IDE in volgens de stijlgids hieronder.

### Dag 2
- Draai de applicatie lokaal en speel met de endpoints via curl of Postman.
- Lees de code van `OrderService` en probeer de statusovergangen te begrijpen.
- Pair met iemand van Team Kameleon op een klein ticket.

### Dag 3 tot 5
- Pak je eerste "good first issue" op.
- Volg de volledige git-workflow (branch, commit, PR, review, merge).
- Schrijf minstens één test. Bij voorkeur meer. Tests zijn documentatie!
- Woon de wekelijkse tech-sync bij en stel jezelf voor.

## 🏛️ Architectuur (overzicht)

De Order App volgt een klassieke gelaagde architectuur. In een eerdere versie hadden we
een hexagonale architectuur met ports en adapters, maar die hebben we terug
vereenvoudigd omdat het voor dit project te veel ceremonie gaf. Sommige oude
documentatie verwijst nog naar `adapter/in/web` en `adapter/out/persistence`; negeer dat.

Lagen, van buiten naar binnen:
1. **Controller-laag**: ontvangt HTTP-requests, valideert input, roept service aan.
2. **Service-laag**: bevat alle businesslogica en statusovergangen.
3. **Repository-laag**: persistentie (vroeger JPA, nu in memory, zie verder).
4. **Model-laag**: domeinobjecten.

Regel: een laag mag enkel de laag direct onder zich aanroepen. Controllers praten dus
nooit rechtstreeks met de repository. Dit is heel belangrijk en wordt streng
gecontroleerd in code reviews.

## 🗄️ Database (PostgreSQL)

> 📝 TODO: deze sectie nakijken na de migratie.

De applicatie gebruikt PostgreSQL 14 als primaire database. Lokaal start je die met
`docker compose up -d postgres`. Het schema wordt beheerd met Flyway; migraties staan in
`src/main/resources/db/migration` en volgen het patroon `V<nummer>__<beschrijving>.sql`.

Regels voor migraties:
- Wijzig nooit een bestaande migratie die al op een gedeelde omgeving gedraaid heeft.
- Elke migratie moet idempotent zijn waar mogelijk.
- Gebruik `snake_case` voor tabel- en kolomnamen.
- Elke tabel heeft een `id BIGSERIAL PRIMARY KEY`, `created_at` en `updated_at`.
- Indexen krijgen de naam `idx_<tabel>_<kolom>`.
- Foreign keys krijgen de naam `fk_<tabel>_<referentie>`.

De tabel `orders` heeft de kolommen `id`, `description`, `status`, `customer_id`,
`created_at`, `updated_at` en `version` (voor optimistic locking via `@Version`).
De tabel `customers` wordt beheerd door Team Pelikaan; daar schrijven we nooit naar.

Connectiegegevens voor de lokale database staan in `application-local.yml`. Vraag de
waarden aan Team Kameleon; die mogen nooit in git.

## 📨 Events (Kafka)

Bij elke statuswijziging publiceren we een event op het topic `orders.status-changed.v2`
(het topic `orders.status-changed` zonder versie is deprecated maar wordt nog door de
facturatie-module gelezen, dus publiceer voorlopig naar beide).

Event-formaat (Avro, schema in `src/main/avro/OrderStatusChanged.avsc`):

```json
{
  "orderId": 42,
  "oldStatus": "ORDERED",
  "newStatus": "SHIPPED",
  "occurredAt": "2025-03-14T10:15:30Z",
  "correlationId": "abc-123"
}
```

Afspraken:
- Gebruik de `OrderEventPublisher`, nooit rechtstreeks `KafkaTemplate`.
- Events worden gepubliceerd via het outbox-patroon (tabel `order_outbox`).
- Consumers moeten idempotent zijn: dezelfde event kan meerdere keren binnenkomen.
- Lokaal start je Kafka met `docker compose up -d kafka zookeeper`.
- Bij integratietests gebruik je Testcontainers met de Kafka-module.

## ☕ Java Stijlgids

Deze stijlgids is gebaseerd op de bedrijfsbrede Java-standaard, met enkele
projectspecifieke aanpassingen. Bij twijfel volgt de bedrijfsbrede standaard.

### Formattering
- Indentatie: 4 spaties, geen tabs.
- Maximale regellengte: 120 karakters (sommige oudere bestanden gebruiken 100, laat die zo).
- Accolades op dezelfde regel (K&R-stijl).
- Eén statement per regel.
- Lege regel tussen methodes.
- Geen trailing whitespace.
- Bestanden eindigen met een newline.
- Gebruik altijd accolades, ook bij een `if` met één statement.

### Naamgeving
- Klassen: `PascalCase`, zelfstandig naamwoord (`OrderService`, niet `ManageOrders`).
- Interfaces: geen `I`-prefix (`OrderRepository`, niet `IOrderRepository`).
- Methodes: `camelCase`, werkwoord (`findById`, `ship`).
- Variabelen: `camelCase`, beschrijvend. Geen afkortingen behalve `id`, `dto`, `url`.
- Constanten: `UPPER_SNAKE_CASE`.
- Packages: lowercase, geen underscores.
- Booleans beginnen met `is`, `has`, `can` of `should`.
- Generieke types: één hoofdletter (`T`, `E`, `K`, `V`).

### Imports
- Geen wildcard-imports (`import java.util.*` is verboden).
- Volgorde: statische imports, dan `java.*`, dan `javax.*`, dan `org.*`, dan `com.*`, dan de rest.
- Ongebruikte imports verwijderen.
- Gebruik de import-volgorde van Spotless (die kan afwijken van bovenstaande, Spotless wint).

### Klassen en methodes
- Maximaal 300 regels per klasse.
- Maximaal 30 regels per methode, bij voorkeur minder dan 20.
- Maximaal 4 parameters per methode; gebruik anders een parameter-object.
- Gebruik constructor injection, nooit `@Autowired` op velden.
- Velden zijn `private final` waar mogelijk.
- Gebruik `record` voor DTO's en request/response-objecten.
- Gebruik Lombok `@Data` en `@Builder` voor entities.
- Gebruik geen Lombok (we zijn ervan afgestapt, zie changelog).
- Vermijd `static` methodes behalve in utility-klassen.
- Utility-klassen zijn `final` met een private constructor.

### Null-handling
- Retourneer nooit `null` uit een publieke methode; gebruik `Optional`.
- Gebruik `Optional` nooit als parameter of veld.
- Valideer input aan de rand van het systeem (controller of service).
- Gebruik `Objects.requireNonNull` in constructors.

### Exceptions
- Gebruik unchecked exceptions voor businessfouten.
- Vang nooit `Exception` of `Throwable` tenzij op het hoogste niveau.
- Log een exception óf gooi hem opnieuw, nooit allebei.
- Exception-boodschappen zijn in het Engels.
- Maak per foutsituatie een eigen exception-klasse.

### Logging
- Gebruik SLF4J, nooit `System.out.println`.
- Gebruik parameterized logging: `log.info("Order {} shipped", id)`.
- Log nooit persoonsgegevens.
- Niveaus: `ERROR` voor onverwachte fouten, `WARN` voor herstelbare problemen,
  `INFO` voor businessgebeurtenissen, `DEBUG` voor technische details.

### Commentaar en Javadoc
- Elke publieke klasse en methode heeft Javadoc.
- Schrijf commentaar enkel waar de code het niet zelf kan uitleggen.
- Geen uitgecommentarieerde code committen.
- `TODO`-commentaar bevat altijd een ticketnummer.

## 🤖 Order App

Spring Boot 3.5 REST API voor bestellingen, Java 17, data in memory.

### Commands
- Build: `./mvnw package` · Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`, daarna `curl localhost:8080/orders`
- Format één bestand: `./mvnw -q spotless:apply -DspotlessFiles=<path>`

### Structuur
Code staat in `src/main/java/be/nexios/orders/`:
- `controller/`: REST-controllers, request-records, `OrderExceptionHandler`
- `service/`: businesslogica, statusovergangen en domein-exceptions
- `repository/`: in-memory `OrderRepository`, seed-data in `OrderDataLoader`
- `model/`: `Order`, `OrderStatus`

### Conventies
- Controllers in `controller/`, services in `service/`
- Nooit rechtstreeks repository-calls vanuit controllers
- Fouten als exception uit de service; `OrderExceptionHandler` zet ze om naar 400, 404 of 409
- Plain JSON, geen HATEOAS

### Gotchas
- Order-status is een enum `OrderStatus`, niet hardcoden
- Geldige overgangen: ORDERED → SHIPPED → DELIVERED, en ORDERED → CANCELLED
- Data zit in memory: na een herstart zijn er weer enkel de 2 seed-orders

## 🧪 Testen

Testen is een kernwaarde van ons team. Code zonder tests wordt niet gemerged. We mikken
op minstens 80% line coverage (90% voor de service-laag).

### Testnaamgeving
- Gebruik het patroon `methodeNaam_scenario_verwachtResultaat`,
  bijvoorbeeld `ship_orderIsOrdered_returnsShippedOrder`.
- Testklassen heten `<Klasse>Test` voor unit tests en `<Klasse>IT` voor integratietests.

### Testopbouw
- Gebruik JUnit 5 en Hamcrest-matchers (`assertThat(x, is(y))`).
- Gebruik Given / When / Then als commentaar in elke test.
- Eén assert per test.
- Gebruik `@SpringBootTest` voor controllertests zodat je de echte wiring test.
- Gebruik Testcontainers voor alles wat met de database of Kafka praat.
- Mock nooit wat je niet bezit (dus geen Spring-klassen mocken).

### Nog over testen (afspraak retro sprint 14)
- Testnamen volgen voortaan `shouldX_whenY`, bijvoorbeeld `shouldShipOrder_whenStatusIsOrdered`.
- Gebruik AssertJ in plaats van Hamcrest.
- Controllertests: `@WebMvcTest` met `MockMvcTester`, service gemockt met `@MockitoBean`.
- Arrange / Act / Assert, gescheiden door een lege lijn (geen commentaar).
- Meerdere asserts per test zijn ok als ze over hetzelfde gedrag gaan.

## 🌐 API Documentatie

Base URL lokaal: `http://localhost:8080`. Alle responses zijn JSON. Datums in ISO-8601 (UTC).

### GET /orders
Geeft alle orders terug.

Request:
```bash
curl -s localhost:8080/orders
```

Response `200 OK`:
```json
[
  { "id": 1, "description": "MacBook Pro", "status": "ORDERED", "createdAt": "2025-03-14T10:15:30Z" },
  { "id": 2, "description": "iPhone", "status": "SHIPPED", "createdAt": "2025-03-14T10:15:30Z" }
]
```

### GET /orders/{id}
Geeft één order terug.

Request:
```bash
curl -s localhost:8080/orders/1
```

Response `200 OK`:
```json
{ "id": 1, "description": "MacBook Pro", "status": "ORDERED", "createdAt": "2025-03-14T10:15:30Z" }
```

Response `404 Not Found`:
```text
Could not find order 99
```

### POST /orders
Maakt een nieuwe order aan met status `ORDERED`.

Request:
```bash
curl -s -X POST localhost:8080/orders \
  -H 'Content-Type: application/json' \
  -d '{"description": "Magic Keyboard"}'
```

Response `201 Created` (met `Location: /orders/3`):
```json
{ "id": 3, "description": "Magic Keyboard", "status": "ORDERED", "createdAt": "2025-03-14T10:16:02Z" }
```

Response `400 Bad Request` bij lege beschrijving:
```text
Description is required
```

### PUT /orders/{id}/ship
Zet een order van `ORDERED` naar `SHIPPED`.

Request:
```bash
curl -s -X PUT localhost:8080/orders/1/ship
```

Response `200 OK`:
```json
{ "id": 1, "description": "MacBook Pro", "status": "SHIPPED", "createdAt": "2025-03-14T10:15:30Z" }
```

Response `409 Conflict`:
```text
Cannot ship an order with status SHIPPED
```

### PUT /orders/{id}/deliver
Zet een order van `SHIPPED` naar `DELIVERED`.

Request:
```bash
curl -s -X PUT localhost:8080/orders/2/deliver
```

Response `200 OK`:
```json
{ "id": 2, "description": "iPhone", "status": "DELIVERED", "createdAt": "2025-03-14T10:15:30Z" }
```

Response `409 Conflict`:
```text
Cannot deliver an order with status ORDERED
```

### DELETE /orders/{id}/cancel
Annuleert een order. Enkel mogelijk vanuit `ORDERED`.

Request:
```bash
curl -s -X DELETE localhost:8080/orders/1/cancel
```

Response `200 OK`:
```json
{ "id": 1, "description": "MacBook Pro", "status": "CANCELLED", "createdAt": "2025-03-14T10:15:30Z" }
```

Response `409 Conflict`:
```text
Cannot cancel an order with status SHIPPED
```

### Foutformaat
Alle fouten volgen RFC 7807 (`application/problem+json`) met velden `type`, `title`,
`status`, `detail` en `instance`. (Opmerking: de huidige implementatie geeft plain text
terug, dit wordt rechtgezet in een volgende sprint.)

## 🌳 Git Workflow

Een goede git-workflow is de ruggengraat van een gezond team. We hebben hier lang over
gediscussieerd en zijn tot onderstaande afspraken gekomen. Ze zijn niet perfect, maar
ze werken voor ons, en consistentie is belangrijker dan perfectie.

### Branching
We gebruiken een variant van Git Flow. Er is een `main`-branch die altijd deploybaar is,
en een `develop`-branch waar features in samenkomen. Feature branches vertrekken van
`develop` en heten `feature/<ticket>-<korte-beschrijving>`. Bugfixes heten
`bugfix/<ticket>-<beschrijving>`, hotfixes vertrekken van `main` en heten
`hotfix/<ticket>-<beschrijving>`.

Update (Q3): we zijn overgestapt op trunk-based development. Branches vertrekken van
`main`, leven maximaal twee dagen en heten `<ticket>-<beschrijving>`. De `develop`-branch
bestaat nog maar wordt niet meer gebruikt. Of toch niet meer voor nieuwe features.

### Commits
- Gebruik Conventional Commits: `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`.
- Onderwerpregel maximaal 50 karakters, in de gebiedende wijs.
- Schrijf commitberichten in het Nederlands.
- Schrijf commitberichten in het Engels (afspraak met Team Pelikaan voor gedeelde repos).
- Vermeld altijd het ticketnummer in de commit.
- Maak kleine, atomaire commits. Eén logische wijziging per commit.
- Squash je commits voor het mergen. Of niet, als de historiek nuttig is.

### Pull Requests
Een pull request is een gesprek, geen formaliteit. Neem de tijd om een goede
beschrijving te schrijven: wat verandert er, waarom, hoe heb je het getest, en waar
moet de reviewer extra op letten. Voeg screenshots of curl-voorbeelden toe waar zinvol.

- Minstens twee approvals nodig (één als het een kleine wijziging is).
- De auteur merget zelf na approval.
- De reviewer merget na approval.
- Los alle review-commentaren op voor je merget, of leg uit waarom niet.
- De pipeline moet groen zijn. Flaky tests mag je één keer herstarten.
- Hou PR's klein: maximaal 400 gewijzigde regels.

### Code Reviews
Bij een code review kijk je naar correctheid, leesbaarheid, testdekking, naleving van
deze stijlgids en de architectuurregels. Wees vriendelijk en constructief. Gebruik
prefixes zoals `nit:`, `vraag:` en `blocker:` om het gewicht van je opmerking aan te
geven. Onthoud: je reviewt de code, niet de persoon.

## ✅ Definition of Done

- [ ] Code geschreven volgens de stijlgids
- [ ] Unit tests geschreven en groen
- [ ] Integratietests geschreven (Testcontainers) en groen
- [ ] Flyway-migratie toegevoegd indien nodig
- [ ] Kafka-event schema bijgewerkt indien nodig
- [ ] API-documentatie in dit bestand bijgewerkt
- [ ] Changelog bijgewerkt
- [ ] PR goedgekeurd door twee reviewers
- [ ] Gedeployed naar test en gevalideerd door de PO

## 🧠 Extra instructies voor de AI-assistent

- Lees altijd eerst dit volledige bestand.
- Volg altijd de stijlgids.
- Schrijf altijd tests (zie Testen, beide secties).
- Gebruik altijd Lombok voor nieuwe klassen.
- Vraag altijd bevestiging voor je een bestand wijzigt.
- Werk zelfstandig en vraag zo weinig mogelijk bevestiging.
- Vermeld bij elke wijziging welke regel uit dit document je toepast.
- Hou je antwoorden kort.
- Leg je redenering altijd volledig uit.
- Gebruik nooit `System.out.println`.
- Controllers mogen nooit rechtstreeks de repository aanroepen.
- Order-status is een enum, nooit hardcoden.
- Schrijf code in het Engels en commentaar in het Nederlands.
- Gebruik geen emoji's in code of commits.

## 📜 Changelog

### v2.4.0
- Overgestapt van PostgreSQL naar in-memory opslag voor de demo-omgeving.
- `OrderDataLoader` toegevoegd met 2 seed-orders.
- Lombok verwijderd uit het project.

### v2.3.0
- `DELETE /orders/{id}/cancel` toegevoegd.
- `OrderStatusConflictException` toegevoegd, mapt naar 409.

### v2.2.0
- Kafka outbox-patroon ingevoerd.
- Topic `orders.status-changed.v2` toegevoegd.

### v2.1.0
- Upgrade naar Spring Boot 3.x en Java 17.
- `javax.*` vervangen door `jakarta.*`.

### v2.0.0
- Hexagonale architectuur teruggedraaid naar gelaagde architectuur.
- HATEOAS verwijderd, plain JSON.

### v1.5.0
- Flyway ingevoerd.
- Optimistic locking op `orders` via `@Version`.
