# Order App (Java starter)

Starter repo for the Claude Code bootcamp: a small Spring Boot REST API for orders (`/orders`), in-memory, seeded with 2 orders.

- Prerequisites: Java 17 or later, git, jq (for the hooks). Windows: run the hooks via Git Bash.
- Run: `./mvnw spring-boot:run`, then `curl localhost:8080/orders`
- Test: `./mvnw test`
- Run the tests once before 15:15 so the Maven cache is warm (the Stop hook runs them).
- Format: `./mvnw spotless:apply`
- Checkpoint branches hold the state at the start of each block: `cp/1300-plan-mode`, `cp/1335-context`, `cp/1445-subagents`, `cp/1515-hooks`, `cp/1540-mini-run`.
- Jump to a checkpoint: `git stash -u && git switch cp/<block>`

Based on [spring-guides/tut-rest](https://github.com/spring-guides/tut-rest) (`links/` module), Apache-2.0. See `LICENSE` and `LICENSE.code.txt`.
