# ZenQ — Data Model

Table design for the core system, informed by the decisions in `docs/BUSINESS_CASE.md` ("in scope
now" items are baked into the schema even though we build incrementally, starting with `tickets`
only, so it isn't painful to retrofit later).

## Entities

```
Client (organization)
  id            UUID (PK)
  name          varchar
  created_at    timestamp

ClientUser (a person at a client org — "client = org with multiple users")
  id            UUID (PK)
  client_id     UUID (FK -> Client)
  email         varchar
  role          varchar        -- e.g. ADMIN, MEMBER
  created_at    timestamp

Product
  id            UUID (PK)
  name          varchar        -- e.g. "Project Ferma"
  slug          varchar        -- e.g. "ferma"

ClientProductAccess (which products a client is scoped to — enforces multi-tenancy)
  client_id     UUID (FK -> Client)
  product_id    UUID (FK -> Product)
  PRIMARY KEY (client_id, product_id)

Ticket
  id                 UUID (PK)
  client_id          UUID (FK -> Client)
  product_id         UUID (FK -> Product)
  requester_user_id  UUID (FK -> ClientUser, nullable)
  subject            varchar
  description        text
  channel            varchar        -- WEB_FORM, EMAIL, SLACK, ...
  status             varchar        -- PENDING, PROCESSED, RESOLVED
  category           varchar        -- assigned by classifier
  priority           varchar        -- assigned by classifier
  embedding          vector         -- pgvector, generated from subject+description
  suggested_response text
  resolution_type    varchar        -- nullable; e.g. AUTO_RESOLVED, HUMAN_RESOLVED,
                                     -- DOC_GAP (see docs/IDEAS.md auto-provisioning), FEATURE_REQUEST
  assigned_agent     varchar        -- nullable
  created_at         timestamp
  updated_at         timestamp

KnowledgeDoc (curated knowledge base — separate RAG source from raw ticket history,
              see "in scope now" in docs/BUSINESS_CASE.md)
  id            UUID (PK)
  product_id    UUID (FK -> Product)
  title         varchar
  content       text
  embedding     vector         -- pgvector
  doc_version   varchar        -- tracks which version an answer came from (doc-drift prevention)
  updated_at    timestamp
```

## Relationships

- `Client` 1—N `ClientUser`
- `Client` N—M `Product` via `ClientProductAccess`
- `Client` 1—N `Ticket`, `Product` 1—N `Ticket`
- `Product` 1—N `KnowledgeDoc`
- "Similar tickets" and "similar docs" are **not** stored as foreign keys — they're computed at
  query time via pgvector similarity search over `embedding`, not a fixed relationship.

## Build order (incremental)

Even though the full schema is designed now, it isn't built all at once. This is the authoritative
build sequence — README's roadmap section is being rewritten and doesn't currently repeat it:

1. **Week 1**: `Ticket` only, with `client_id`/`product_id` columns present but pointing at a
   single seeded default `Client`/`Product` row — no real multi-tenancy logic yet, just the
   columns existing so adding real tenancy later is a data change, not a schema migration.
2. **Week 2**: add `embedding` to `Ticket`, wire up pgvector, similarity search.
3. **Later**: `KnowledgeDoc` (curated KB), `ClientUser`, real `ClientProductAccess` enforcement,
   auth.

## Notes

- This file lives in `docs/`, which is gitignored (local-only, not pushed).
- Update this file when the actual JPA entities diverge from it — it should track real schema,
  not go stale as a one-time design doc.
