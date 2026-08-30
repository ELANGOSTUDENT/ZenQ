# ZenQ — Deployment Notes

Not an active task — a decision to revisit later, recorded so it isn't re-litigated from scratch.
Deployment matters for the portfolio (a live URL beats "trust me, it runs locally"), but it comes
**after** the core app works locally, not before.

## 1. Sequencing

1. Build the app locally first — no deployment decision blocks anything being built now.
2. A working Docker Compose setup (app + Postgres + RabbitMQ, not yet built — see
   `docs/DATA_MODEL.md` for the schema build order it depends on) is the prerequisite for any
   deployment option below — cloud deployment is just "run that same Docker Compose setup
   somewhere rented."
3. Don't design the cloud architecture until step 2 is done.

## 2. What the common AWS services map to for this project

| Service | What it is | Relevant to ZenQ? |
|---|---|---|
| **EC2** | A rented virtual machine | Simplest deploy path — run the same `docker-compose up` on a rented server instead of a laptop |
| **RDS** | AWS's managed PostgreSQL | Would replace the self-hosted Postgres container in production; RDS Postgres supports the `pgvector` extension, so it fits |
| **S3** | Object storage (files, not a database) | Not needed currently — nothing in the design stores files/attachments; skip unless that changes |
| **Serverless** (Lambda, SQS, etc.) | Not just a hosting choice — a real architectural fork | RabbitMQ doesn't run "serverless"; going this route means swapping it for a managed alternative (Amazon MQ) or redesigning around SQS instead of RabbitMQ |

## 3. Recommendation

1. **Start with EC2 + Docker Compose.** It's the exact setup already being built, just hosted —
   nothing new to learn except getting a server and pointing a domain at it.
2. **Once that works, swap in RDS for the database.** Managed > self-hosted is a real,
   resume-worthy upgrade, and a natural next step once the basics are solid.
3. **Leave serverless/SQS alone for now.** Legitimate to know for a job, but redesigning around
   it now costs real time for something EC2 + RDS already covers for portfolio purposes.
4. Skip S3 entirely unless a real file-storage need shows up in the product.

## Notes

- This file lives in `docs/`, which is gitignored (local-only, not pushed).
- Revisit this once the app runs locally end-to-end via Docker Compose.
