# Memory.md — Living Project Memory

> This file is NOT filled out at project start. Create it once coding begins, and update it after every meaningful work session/phase. Its purpose: let the AI (in a new chat/tool/session) resume work without re-reading the entire codebase or guessing at prior decisions.

---

## How to Use This File
1. After completing a task, unit of work, or phase, append an entry to **Progress Log**.
2. Update **Current State** to reflect what's true *right now* (not historical).
3. Record any **Decisions & Deviations** from `PRD.md` / `Architecture.md` / `Rules.md` — including why.
4. Record **Known Issues / TODOs** so nothing is silently forgotten.
5. Keep entries short and factual — this file is for machine context, not prose.

---

## Current State (update in place, don't append)

**Last updated:** _(date)_
**Current phase:** _(e.g., Phase 3 — Restaurant & Branch Management)_
**Backend status:** _(e.g., builds clean, X endpoints implemented, Y tests passing)_
**Frontend status:** _(e.g., admin shell + auth pages done, menu CRUD in progress)_
**Database status:** _(last migration version, e.g., V6__add_offers_table.sql)_
**Environment:** _(dev only / dev+staging / deployed to prod)_

---

## Progress Log
_(Append new entries at the top, most recent first. One entry per session/phase.)_

### YYYY-MM-DD — Phase 0 Bootstrap
- Initialized Spring Boot project with Web, Security, Data JPA, Validation, MySQL, Flyway.
- Initialized Angular workspace with base folder structure per `Architecture.md`.
- Set up `application.yml` profiles (dev/staging/prod) with env-var placeholders.
- Exit criteria met: app boots, health check returns 200, Angular shell serves.

---

## Decisions & Deviations from Original Docs
_(Record anything that differs from PRD/Architecture/Rules, and why.)_

- _Example: Chose Cloudinary over S3 for image storage in MVP — cheaper at low volume, revisit at Professional-tier scale (per Architecture.md Phase 8 options)._

---

## Known Issues / Open TODOs
- _Example: Bulk menu-item upload does not yet validate duplicate item names within the same category — flagged for Phase 4 follow-up._

---

## Key Identifiers & Reference Values
_(Things a future session would otherwise have to dig through code to find.)_

- JWT access token TTL: _(e.g., 15 min)_ / refresh token TTL: _(e.g., 7 days)_
- Roles in use: `SUPER_ADMIN`, `RESTAURANT_OWNER`, `MANAGER`, `STAFF`
- Public menu URL pattern: `https://menu.yourdomain.com/menu/{slug}`
- Current Flyway migration head: _(e.g., V6)_
- Payment providers wired: _(e.g., Razorpay sandbox only, PayPal not yet started)_

---

## Next Session Should Start With
_(One or two lines telling the next session exactly where to pick up.)_

- _Example: Continue Phase 4 — menu item bulk upload endpoint is scaffolded but CSV row-level error reporting is not implemented yet (see Known Issues)._
