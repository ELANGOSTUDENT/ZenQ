# ZenQ

An async, event-driven support ticket triage service built in Java/Spring Boot. Incoming tickets are classified, matched against similar past tickets using vector search (RAG), and given a suggested response — all processed asynchronously via a message queue.

## Status
In early development. Project scaffolding in progress.

## Planned architecture
- **API layer**: Spring Boot REST endpoints for ticket ingestion and status lookup
- **Queue**: RabbitMQ for decoupled, async processing
- **Storage**: PostgreSQL for ticket data, pgvector for embeddings
- **AI**: LLM-based classification + RAG-based similar-ticket retrieval and response suggestion (via Spring AI)
- **Deployment**: Docker Compose (app + Postgres + RabbitMQ)

## Roadmap
- [ ] Week 1: Spring Boot skeleton, ticket CRUD, Postgres schema, RabbitMQ producer/consumer wiring
- [ ] Week 2: pgvector setup, embedding generation, similarity search, LLM classification
- [ ] Week 3: Idempotency/retry/dead-letter handling, tests, Docker Compose, architecture diagram

## Tech stack
Java, Spring Boot, Spring Data JPA, RabbitMQ, PostgreSQL, pgvector, Spring AI, Docker
