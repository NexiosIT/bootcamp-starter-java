# Intent: Bestelstatus opvolgen
Doel: Klant ziet de actuele status van zijn bestelling zonder support te contacteren.
Waarom: 40% van de supporttickets gaat over "waar is mijn bestelling".
Scope: statuspagina per bestelling (besteld → verzonden → geleverd).
Buiten scope: notificaties, track & trace van de vervoerder.
Constraints: enkel ingelogde klant ziet eigen bestellingen; bestaande Order-API hergebruiken.
Definition of done:
- GET /orders/{id}/status geeft status + tijdstip terug
- 403 bij bestelling van andere klant
- Statuspagina toont tijdlijn
- Tests groen
