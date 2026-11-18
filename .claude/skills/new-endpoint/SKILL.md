---
name: new-endpoint
description: Use when adding a new REST endpoint to the Order App, e.g. "nieuwe endpoint", "API om bestelling te annuleren".
---
1. Lees `controller-template.md` in deze skill-folder
2. Maak de controller-methode in `controller/` en de logica in `service/`, volgens de structuur in CLAUDE.md
3. Als Order een klant-id heeft: filter op de ingelogde klant (klant ziet enkel eigen data). Anders sla deze stap over.
4. Schrijf tests volgens de rule voor tests
5. Run `./mvnw test` en fix tot alles groen is
