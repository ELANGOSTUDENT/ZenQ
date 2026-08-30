# ZenQ — Docs Overview

Table of contents for `docs/`. Start here.

| File | What it is |
|---|---|
| **`BUSINESS_CASE.md`** | The source of truth for scope decisions: positioning, who this serves, differentiation vs. Jira/Zendesk, problem→solution mapping, real-world gap review, and recommended additions. Business content only. |
| `DATA_MODEL.md` | Table design (Client, ClientUser, Product, Ticket, KnowledgeDoc), relationships, and the incremental build order. Directly maps to the JPA entities. |
| `UML.md` | Class, use case, and sequence diagrams for ZenQ, with plain-language notation explainers. |
| `IDEAS.md` | **Archived / reference only** — rejected/parked feature ideas, kept for later, not the active plan. |
| `DEPLOYMENT.md` | Not an active task — deployment sequencing and AWS service recommendations (EC2, RDS, S3, serverless), to revisit after the app runs locally via Docker Compose. |

`README.md` (repo root, not gitignored) is being rewritten separately — everything in `docs/` is
local-only (gitignored, not pushed), so it won't be visible to anyone cloning the repo. Keep that
in mind: anything the outside world needs to see (setup instructions, a real project description)
has to live in `README.md` itself, not just point at `docs/`.

## Notes

- This file lives in `docs/`, which is gitignored (local-only, not pushed).
