# ZenQ — Feature Ideas & Roadmap (Archived / Reference)

> **Status (2026-08-30): archived, not deleted.** These are rejected/parked ideas kept for
> reference — not the active plan. The current source of truth for scope decisions is
> `docs/BUSINESS_CASE.md`. Nothing here is committed work; treat this file as an idea log to pull
> from later, not a roadmap being executed now.

This consolidates the original project plan (from `README.md`) with the expanded feature
brainstorm from 2026-08-30. It's meant as a living list — the core plan is what we're building
first; everything else is a documented idea to pull from later, not scope we've committed to yet.

## Reframe (2026-08-30): AI knowledge portal, not a help desk

"Support ticket helpdesk" is the wrong headline — it reads as the old, passive Jira/Zendesk model
(submit a ticket, wait). The actual product is a **client-facing portal** where clients ask
questions and get instant answers from company knowledge — tickets only get created as a
**fallback**, when the portal can't answer directly. Nothing about the underlying engineering
changes because of this — classification, RAG, agentic auto-resolution, reliability,
multi-tenancy are all still needed — this is a repositioning plus two real additions:

1. **Single MCP server, not one per product.** All of the company's products' documentation is
   exposed through one MCP server, scoped per client/product via access control. This is simpler
   to operate than N servers (one deployment, one place access control lives, and a client on
   more than one product isn't a special case) — see the multi-product section below, which now
   assumes this single-server design.
2. **The client portal is explicit, first-class scope** — an actual UI people log into, not just
   a capability described in the backend. This means a frontend layer is now part of the plan
   (frontend framework still TBD), not only Spring Boot APIs.

## Core plan (original, from README)

- **API layer**: Spring Boot REST endpoints for ticket ingestion and status lookup
- **Queue**: RabbitMQ for decoupled, async processing
- **Storage**: PostgreSQL for ticket data, pgvector for embeddings
- **AI**: LLM-based classification + RAG-based similar-ticket retrieval and response suggestion (via Spring AI)
- **Deployment**: Docker Compose (app + Postgres + RabbitMQ)

Target metrics this needs to hit: ~87% RAG retrieval accuracy on a 250-ticket labeled eval set,
~60% reduction in manual triage effort, ~600 req/sec at p95 150ms with 99.9% delivery reliability
under RabbitMQ retry/DLQ handling. (These are also restated in `docs/BUSINESS_CASE.md` §4.)

## Chosen next differentiator: Agentic auto-resolution

Instead of only *suggesting* a reply, give the AI tool-calling access to actually resolve simple
tickets — reset a password, check order status, issue a refund under a threshold — via function
calling. This is the biggest capability jump: it turns ZenQ from "drafts text for a human" into
"resolves tickets end to end for the cases it's confident about," with everything else still
routed to a human. This builds on top of the core plan (classification + retrieval), it doesn't
replace it — it's the next layer once ingestion → classify → retrieve → suggest is working.

## Major future idea: multi-product, client-scoped self-service (2026-08-30)

ZenQ today is scoped to one product. The bigger version: ZenQ serves **N products** (e.g. 10),
all of whose documentation is exposed through **one company-wide MCP server** (not one server per
product — see the reframe note above), and each client is scoped to only the product(s) they're
associated with.

1. **Client-scoped access**: Client A is only associated with "Project Ferma" and can only see/
   query Ferma's docs and tickets. Client B is only associated with "Project Curie" and can only
   see Curie's. This is a permissions/multi-tenancy boundary enforced *within* the single MCP
   server (e.g. scoping which resources a client's calls can reach) — every query, MCP call, and
   ticket needs to carry "which client, which product" and enforce it.
2. **Self-service check before ticket creation**: a client can ask/search whether a feature
   already exists in *their* product. ZenQ answers directly from that product's docs (via MCP +
   RAG) if the answer is already there — no ticket needed.
3. **Auto-drafted ticket on a gap**: if the feature/answer isn't found, ZenQ auto-generates a
   draft ticket capturing what the client was looking for, instead of the client writing one from
   scratch. The client reviews/edits the draft, then submits.
4. **Dev-facing recommendation**: because ZenQ already has full product context via MCP, when the
   ticket reaches the developer it doesn't just forward the raw client request — it also
   recommends what likely needs to change, so the developer isn't scoping the work from zero.

**Sequencing note**: #1 (multi-tenancy/access control) is a significant piece of engineering on
its own, not a small add-on — it changes the shape of nearly everything else in the system. Build
this *after* the core single-product pipeline (ingest → classify → retrieve → suggest) is working
for one product, not alongside it.

### Auto-provisioning: preventing documentation drift (2026-08-30)

The multi-product idea depends on ZenQ's knowledge base staying in sync with what's actually
shipped. The real risk: a developer ships or updates a feature, forgets to update the docs ZenQ
reads from, and ZenQ starts telling clients "that doesn't exist" for something that does — or
worse, auto-drafts a duplicate feature-request ticket for it. Relying on "the developer
remembers" is exactly the failure mode to design around, not hope doesn't happen. Three layers,
used together:

1. **Prevent it at the source (deploy-time auto-provisioning)**: shipping a new/updated feature
   requires a small structured "feature descriptor" (name, what it does, how to use it) as part
   of the deploy. The deploy pipeline blocks the deploy if a new feature/endpoint ships without
   one — an enforced gate, not a reminder. On successful deploy, the descriptor is automatically
   ingested into that product's knowledge base, so the knowledge base updates as a side effect of
   shipping, not a separate manual writing step.
2. **Catch what slips through (feedback loop)**: when client self-service search comes up empty
   and a ticket is auto-drafted, and a developer resolves it by saying "this already exists"
   rather than building something new — that's a direct signal the *documentation* was missing,
   not the feature. Capture that distinction explicitly (developer marks resolution type: "was
   missing" vs. "wasn't documented"). When it's the latter, require the developer to add the
   descriptor immediately, which re-ingests it right away — closing the loop so the next client
   doesn't hit the same gap.
3. **Audit on a schedule (safety net)**: a periodic job compares the live feature/API surface
   (routes, feature flags actually in production) against what's in the knowledge base, and flags
   anything live with zero doc coverage — catching gaps before a client ever hits them.

**Metric to track**: "documentation-gap tickets" (case 2 above) as its own count, separate from
real feature requests. A high number means layer 1 (the deploy gate) isn't being followed and
needs tightening — not that clients are asking for too much.

## Other ideas (documented, not yet scheduled)

- **External MCP consumption**: beyond serving ZenQ's own MCP server, ZenQ could *consume* an
  external MCP server (a CRM/Jira) so triage has real customer context, not just the ticket text.
  Separate from the core knowledge-portal MCP server above — this is about pulling in outside
  context, not exposing ZenQ's own.
- **Multi-agent pipeline**: split classify / retrieve / draft into separate agents, plus a critic
  agent that scores draft quality before it reaches a human. Gives a natural place to plug in the
  eval-accuracy tracking from the core plan's 87% target.
- **Feedback loop**: track agent edits to suggested replies vs. what was actually sent, feed that
  back into retrieval/prompting over time — closes the loop on the 87%-accuracy target on an
  ongoing basis instead of measuring it once and forgetting it.
- **Multi-channel ingestion**: accept tickets from email, Slack, and a web form, all feeding the
  same async pipeline via different producers.
- **SLA-based auto-escalation**: priority scoring feeds into on-call paging / SLA-breach alerts.
- **Analytics dashboard**: ticket volume trends, category breakdown, SLA compliance, agent
  performance — built over data the pipeline is already collecting.

## Notes

- This file lives in `docs/`, which is gitignored (local-only, not pushed).
- When picking up a new idea from this list, move its scope into the README roadmap so it's
  tracked as committed work, not just an idea.
