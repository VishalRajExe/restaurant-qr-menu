# Memory.md — Living Project Memory

> This file is NOT filled out at project start. Create it once coding begins, and update it after every meaningful work session/phase. Its purpose: let the AI (in a new chat/tool/session) resume work without re-reading the entire codebase or guessing at prior decisions.

---

## How to Use This File
1. After completing a task, unit of work, or phase, append an entry to **Progress Log**.
2. Update **Current State** to reflect what's true *right now* (not historical).
3. Record any **Decisions & Deviations** from `PRD.md` / `Architecture.md` / `Rules.md` — including why.
4. Record any **Known Issues / TODOs** so nothing is silently forgotten.
5. Keep entries short and factual — this file is for machine context, not prose.

---

## Current State (update in place, don't append)

**Last updated:** _2026-07-20_
**Current phase:** _Phase 1 — Database Design (in progress)_
**Backend status:** _Spring Boot application builds successfully, Angular workspace created and both applications build successfully_
**Frontend status:** _Angular workspace with two applications (public-menu and admin) created and built successfully_
**Database status:** _H2 in-memory database used for development (schema defined via Flyway migration)_
**Environment:** _Development (H2)_

---

## Progress Log
### 2026-07-20 — Phase 1 — Database Design (in progress)
- Created initial Flyway migration V1__init.sql with tables: restaurant, branch, category, menu_item, offers, qr_codes, users, scan_events.
- Added audit columns (created_at, updated_at, deleted_at, is_deleted) to all tables.
- Added foreign keys and indexes for tenant scoping (restaurant_id, branch_id, category_id).
- Verified that JPA entities and repositories already exist.

### 2026-07-20 — Phase 0 Bootstrap
- Restructured backend package structure to match Architecture.md (com.restaurantqr.platform).
- Updated all import statements accordingly.
- Created Angular workspace with two applications: public-menu and admin.
- Built both Angular applications successfully.
- Updated configuration to use H2 in-memory database for development.
- Verified that the Spring Boot application compiles and the Angular applications build.

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
_(Things a future session would otherwise have to dig through code to find.)_

- _Example: Bulk menu-item upload does not yet validate duplicate item names within the same category — flagged for Phase 4 follow-up._
- **SECURITY ISSUE**: Public self-registration endpoint allows privilege escalation to RESTAURANT_OWNER role (Critical) - **ADDRESSED IN PLANNING PHASE**
- **SECURITY ISSUE**: Cloudinary API key has default value that could be exposed (Medium) - **ADDRESSED IN PLANNING PHASE**

---

## Key Identifiers & Reference Values
_(Things a future session would otherwise have to dig through code to find.)_

- JWT access token TTL: _(e.g., 15 min)_ / refresh token TTL: _(e.g., 7 days)_
- Roles in use: `SUPER_ADMIN`, `RESTAURANT_OWNER`, `MANAGER`, `STAFF`
- Public menu URL pattern: `https://menu.yourdomain.com/menu/{slug}`
- Current Flyway migration head: _(e.g., V1)_
- Payment providers wired: _(e.g., Razorpay sandbox only, PayPal not yet started)_

---

## Next Session Should Start With
_(One or two lines telling the next session exactly where to pick up.)_

- Continue Phase 1 — verify that the Flyway migration applies successfully on startup, run the application to ensure tables are created, and columns, verify JPA repositories can perform basic CRUD operations.
- Address security issues identified in P0 audit:
  1. Fix critical vulnerability: Public self-registration allows privilege escalation to RESTAURANT_OWNER
  2. Fix security issue: Remove default value for Cloudinary API key in application.yml

---