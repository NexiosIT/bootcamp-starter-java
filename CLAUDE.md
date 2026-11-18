# Order App
Spring Boot 3.5 REST API voor bestellingen, Java 17, data in memory.

## Commands
- Build: `./mvnw package` · Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`, daarna `curl localhost:8080/orders`
- Format één bestand: `./mvnw -q spotless:apply -DspotlessFiles=<path>`

## Structuur
Code staat in `src/main/java/be/nexios/orders/`:
- `controller/`: REST-controllers, request-records, `OrderExceptionHandler`
- `service/`: businesslogica, statusovergangen en domein-exceptions
- `repository/`: in-memory `OrderRepository`, seed-data in `OrderDataLoader`
- `model/`: `Order`, `OrderStatus`

## Conventies
- Controllers in `controller/`, services in `service/`
- Nooit rechtstreeks repository-calls vanuit controllers
- Fouten als exception uit de service; `OrderExceptionHandler` zet ze om naar 400, 404 of 409
- Plain JSON, geen HATEOAS

## Gotchas
- Order-status is een enum `OrderStatus`, niet hardcoden
- Geldige overgangen: ORDERED → SHIPPED → DELIVERED, en ORDERED → CANCELLED
- Data zit in memory: na een herstart zijn er weer enkel de 2 seed-orders
