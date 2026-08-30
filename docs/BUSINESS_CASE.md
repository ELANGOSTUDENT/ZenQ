# ZenQ — Business Case

Point-wise reference. Business content only — data model, UML, and feature-idea details live in
their own files (see `docs/OVERVIEW.md` for the full docs map). Nothing below has been dropped
from earlier versions of this file, only reorganized.

## 1. Positioning

1. ZenQ is an **AI-native knowledge portal**, not a help-desk/ticketing tool.
2. Clients get instant answers from a single, company-wide MCP-connected knowledge base.
3. A ticket is created only as a **fallback**, when the portal can't answer — it's not the
   primary interface.
4. This positioning makes the Jira/Zendesk comparison in §3 sharper, not weaker — those tools
   have no equivalent of "the client usually doesn't need to file anything at all."

## 2. Who this serves, and what each needs

### 2.1 Internal (the company running ZenQ / support ops team)

1. Cut agent hours spent manually reading and categorizing tickets.
2. Consistent categorization/priority — not dependent on which agent happens to triage it.
3. Visibility into ticket volume trends, SLA compliance, and escalation risk.
4. Faster onboarding for new agents — suggested responses double as a built-in knowledge base
   drawn from real past resolutions.
5. Scale ticket volume (~1 lakh users — the project's stated scale target) without scaling
   headcount linearly.

### 2.2 Client / requester (whoever raises the ticket)

1. Faster first response and resolution time.
2. Consistent, accurate answers regardless of which agent handles the ticket.
3. Eventually — some tickets resolved instantly with no human wait, once agentic auto-resolution
   ships (see `docs/IDEAS.md`).
4. Transparent status tracking on their ticket.

## 3. How this differs from Jira / Zendesk / Freshdesk / ServiceNow

Honest framing up front: those are mature commercial platforms — ZenQ isn't competing with them
as a *product*. The real difference is in *approach*, and it's the actual portfolio/interview
point.

1. **AI-native vs. bolted-on**: Jira/Zendesk are ticket-tracking tools first; AI where it exists
   is a generic add-on (trained on broad, not organization-specific, data) or static rule-based
   routing (`if category = X, assign to Y`) needing manual upkeep. ZenQ's RAG suggestions are
   built on the organization's *own* historical resolutions — accuracy improves as ticket history
   grows, instead of relying on generic training data or hardcoded rules.
2. **Agentic auto-resolution as a first-class goal**, not a chatbot bolted onto a tracker — the
   aim is to actually reduce agent workload for well-understood ticket types, not just organize
   tickets more neatly for a human to read.
3. **Async/high-throughput from day one** (RabbitMQ, idempotency, dead-letter handling) rather
   than a synchronous CRUD app with a queue tacked on later — matters at the ~1 lakh user scale
   target.
4. **Self-hosted**: no per-agent licensing cost, full control over data and model choice, no
   vendor lock-in.
5. **Portal-first, not ticket-first**: ZenQ's primary interface answers directly; a ticket only
   gets created when that fails. This is a different product shape, not just a smarter version
   of the same one.

**Caveat to keep honest going forward**: this comparison demonstrates understanding of the gap
between "ticketing software" and "AI-native triage" — it is not a claim that ZenQ is
production-ready to replace Jira/Zendesk. Resume/portfolio language should read "here's how I'd
design this differently," never "this beats Jira."

## 4. Problem → Solution mapping

Each feature exists to solve a specific real-world pain point — tied to a reason, not just listed
as a capability.

1. **Manual triage is slow and inconsistent between agents.**
   - Real world: agents spend hours reading, categorizing, and prioritizing tickets by hand; two
     agents often categorize the same kind of ticket differently.
   - Who it hurts: support team (wasted hours), clients (slower, inconsistent handling).
   - Solution: LLM auto-classifies category + priority on ingestion.
   - Proof metric: ~60% reduction in manual triage effort.

2. **Agents re-solve the same problem from scratch every time.**
   - Real world: the same issue type ("duplicate charge," "can't reset password") gets answered
     from scratch each time; tribal knowledge is lost when agents leave.
   - Who it hurts: support team (wasted effort), clients (slower, less consistent resolutions).
   - Solution: RAG-based similarity search over historical tickets; suggested response drafted
     from real past resolutions.
   - Proof metric: ~87% retrieval accuracy on a 250-ticket labeled eval set.

3. **Simple, mechanical requests still wait on a human.**
   - Real world: password resets, order-status checks, refunds under a threshold all route
     through a human even though the action is mechanical and low-risk.
   - Who it hurts: clients (unnecessary wait), support team (busywork on low-value actions).
   - Solution: agentic auto-resolution via tool-calling, above a confidence threshold, with a
     human-fallback path and a full audit trail of every auto-taken action.
   - Proof metric: % of eligible ticket types resolved with zero human touch, zero un-audited
     actions.

4. **The system needs to survive real traffic, not just work in a demo.**
   - Real world: ticket volume spikes, and a synchronous design degrades or drops requests under
     load.
   - Who it hurts: everyone, once under real usage.
   - Solution: async, queue-based architecture (RabbitMQ) with idempotent retry and dead-letter
     handling.
   - Proof metric: ~600 req/sec at p95 latency 150ms, 99.9% delivery reliability under k6 load
     testing.

5. **Clients across different products get irrelevant answers — or worse, see each other's
   data.**
   - Real world: a company selling N products shouldn't dump every client into one
     undifferentiated knowledge base; a client asking about Product A must never see Product B's
     docs or tickets.
   - Who it hurts: clients (trust, or a serious privacy breach), the company (liability,
     reputational damage).
   - Solution: per-product documentation, with per-client access scoped to only the product(s)
     they're associated with — treated as a security requirement, not a UX filter.
   - Proof metric: zero cross-tenant data leakage (must be explicitly tested); % of client
     questions answered via self-service without a ticket.

6. **Feature requests are vague and need multiple rounds of clarification.**
   - Real world: a client says "we need X," and a developer has to chase down what "X" means
     through several back-and-forth messages before real work starts.
   - Who it hurts: developers (time lost to clarification), clients (frustration, slower
     delivery).
   - Solution: self-service search surfaces "not found" cases as auto-drafted, structured
     tickets the client reviews before submitting; ZenQ also suggests to the developer what
     likely needs to change.
   - Proof metric: reduction in clarification round-trips per feature ticket before development
     work actually starts.

## 5. Diff against real-world support org structure

ZenQ's plan is a legitimate *technical* architecture (async, RAG, queue reliability are genuinely
how production systems are built). What's missing is the organizational reality around it — no
portfolio project should build all of it; a real company spreads this across entire teams (SRE,
compliance, billing, CS ops). The point here is deciding, on purpose, what's in scope now vs.
consciously left out.

| Real-world element | In ZenQ's plan? |
|---|---|
| Escalation tiers (L1 → L2 → L3/on-call) | ❌ Not designed yet |
| SLA tied to client contract/tier, not just ticket content | ❌ Priority is currently content-only |
| Client = organization with multiple users | ❌ Current model treats a client as one atomic entity |
| CSAT / satisfaction feedback loop after ticket close | ❌ Nothing measures client satisfaction |
| Curated knowledge base vs. raw ticket history | ❌ RAG planned only over past tickets |
| Notification/communication channel (email/in-app/SMS) | ❌ Not addressed |
| Auth & roles (client login, admin vs. regular user, internal roles) | ❌ Not addressed |
| Staged rollout for AI-taken actions | ❌ Confidence threshold planned, no rollout strategy |
| Data retention / PII handling | ❌ No retention/deletion policy yet |
| Reporting for managers/leadership | Partial — logged as an idea (`docs/IDEAS.md`), not built |

### 5.1 In scope now (cheap to design in from the start, expensive to retrofit later)

1. Client = organization with multiple users — changes the data model now; painful to bolt on
   after tickets/accounts already exist.
2. Curated knowledge base as a separate RAG source from raw ticket history.
3. Basic auth/roles — even a simple version, before there's real client data to protect.
4. Feature-flag/staged rollout for any auto-resolution action.

### 5.2 Consciously out of scope (a decision, not an oversight)

1. Full compliance/GDPR tooling.
2. On-call paging integrations.
3. Billing/contract system integration.
4. Multi-channel notifications beyond one basic channel.

## 6. Gaps identified in review — recommended additions

Treated as real requirements once the multi-product/agentic pieces are built, not afterthoughts.

1. **Tenant isolation is a security requirement, not just access control.** "Client A only sees
   Product A" must be explicitly tested (e.g. an automated test asserting Client A's queries can
   never return Product B data), not just implemented and assumed correct.
2. **Document ingestion/versioning pipeline is a prerequisite, not a detail.** Docs change over
   time; without tracking which doc version an embedding/answer came from, self-service answers
   go stale silently. Needs its own pipeline (re-ingest on doc change, track doc version per
   embedding).
3. **Agentic auto-resolution needs a trust/safety layer**, not just "AI takes the action":
   - A confidence threshold — only auto-act when the model is sufficiently sure.
   - A human-fallback path for anything below that threshold.
   - A full audit trail of every auto-taken action (what changed, why, when).
4. **The newer ideas need their own success metrics**, same as the core plan already has:
   - Self-service resolution rate: % of client questions answered without a ticket.
   - Auto-draft accuracy: % of auto-drafted tickets the client accepts with minimal edits.
   - Clarification reduction: fewer back-and-forth rounds per feature ticket before dev starts,
     vs. the pre-ZenQ baseline.
   - Zero cross-tenant leakage, tracked as a hard pass/fail security metric.

## Notes

- This file lives in `docs/`, which is gitignored (local-only, not pushed).
- Data model → `docs/DATA_MODEL.md`. UML diagrams → `docs/UML.md`. Feature ideas (including
  archived ones) → `docs/IDEAS.md`. Full docs map → `docs/OVERVIEW.md`.
