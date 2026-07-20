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
**Current phase:** _Phase 1 P0 CRITICAL SECURITY — COMPLETE_
**Backend status:** _Spring Boot application builds and all 15 tests pass (`mvn clean test` ✅ SUCCESS)._
**Frontend status:** _Not in scope — BACKEND ONLY._
**Database status:** _H2 in-memory (dev). Flyway V1__init.sql present._
**Environment:** _Development (H2)_

---

## Progress Log

### 2026-07-20 — Phase 1 — P0 Critical Security (COMPLETE)
- **Resolved P0-1 (JWT Secret Fail-Fast Guard)**: Added `@PostConstruct` guard in `JwtTokenProvider` to validate secret key length ≥ 32 bytes (256 bits).
- **Resolved P0-2 (Subscription Activation Payment Bypass)**: Restricted `POST /subscriptions/restaurants/{id}/activate` to `SUPER_ADMIN` role only. Enforced tenant scoping on `SubscriptionService` methods (`activate`, `cancel`, `getActiveSubscription`, `getHistory`) via `restaurantService.findById`.
- **Resolved P0-3 & P0-4 (GET /restaurants/{id} Info Disclosure & Tenant Bypass)**: Fixed path matcher typo in `SecurityConfig` (`/restaurants/slash/*` -> `/restaurants/slug/*`). Removed permitAll from `GET /restaurants/*` so `/restaurants/{id}` requires authentication. Added `@PreAuthorize` to `RestaurantController.getById`. Updated `RestaurantService.assertRestaurantAccess` to throw `ForbiddenException` on null/unauthenticated/anonymous requests.
- **Resolved P0-5 (JWT Access Token Lifetime)**: Reduced `access-token-expiration` in `application.yml` from 24h (86400000 ms) to 15 minutes (900000 ms) per `Architecture.md`.
- **Verified P0-6 (Restaurant Modification Protection)**: Confirmed `PUT /restaurants/{id}` enforces tenant isolation via `assertRestaurantAccess(id)`.
- **Fixed Test Suite**: Fixed `AuthServiceTest` DTO compile error and updated `PublicMenuControllerIntegrationTest` contextPath. Created `P0SecurityFixesTest`. `mvn clean test` passes 15/15 tests cleanly.
- Produced `docs/audits/SECURITY_AUDIT.md`.

### 2026-07-20 — Phase 0 Audit (READ-ONLY)
- Completed full Phase 0 production-readiness audit of the backend.
- Read PRD.md, Architecture.md, Rules.md, Phases.md, Memory.md.
- Compiled codebase: 76 source files, all pass compilation (`mvn clean compile` ✅).
- Test suite does NOT compile: `AuthServiceTest` uses old `RegisterRequest` type — was not updated when `UserRegistrationDto` was introduced (P1-1).
- Inventoried all 68 endpoints across 13 controllers → `docs/audits/API_INVENTORY.md`.
- Produced full findings report (32 findings across P0-P3) → `docs/audits/INITIAL_AUDIT.md`.
- Corrected package structure in Architecture.md (was `com.restroqr.platform`, actual is `com.restaurantqr.platform`).

### 2026-07-20 — Phase 1 — Security Fixes (Critical)
- **FIXED CRITICAL VULNERABILITY**: Public self-registration privilege escalation to RESTAURANT_OWNER role
  - Created `UserRegistrationDto` in `com.restaurantqr.platform.modules.auth.dto` with validation annotations
  - Removed dangerous `restaurantId` field from `RegisterRequest` class
  - Updated `AuthController.register()` to use `UserRegistrationDto` and hardcode role to `User.Role.STAFF`
  - Updated `AuthService.register()` to accept `UserRegistrationDto` parameter
  - Verified no remaining references to `RegisterRequest` in service/controller layers
- **IMPLEMENTED SECURITY HEADERS** (CSP, HSTS, etc.)
  - Created `SecurityHeadersFilter` in `com.restaurantqr.platform.config`
  - Added filter to `SecurityConfig` to apply before `UsernamePasswordAuthenticationFilter`
  - Configured headers: HSTS, X-Content-Type-Options, X-Frame-Options, X-XSS-Protection, CSP, Referrer-Policy, Permissions-Policy
- **IMPLEMENTED RATE LIMITING** (Bucket4j)
  - `RateLimitFilter`: auth=20/min, public=120/min per IP — CONFIRMED IN CODE ✅
- **ENHANCED INPUT VALIDATION** (partial)
  - Applied validation annotations (`@NotBlank`, `@Email`, `@Size`) to `UserRegistrationDto`
  - Foundation laid for extending DTO validation to other endpoints

### 2026-07-20 — Phase 1 — Database Design (in progress)
- Created initial Flyway migration V1__init.sql with tables: restaurant, branch, category, menu_item, offers, qr_codes, users, scan_events.
- Added audit columns (created_at, updated_at, deleted_at, is_deleted) to all tables.
- Added foreign keys and indexes for tenant scoping (restaurant_id, branch_id, category_id).
- **NOTE: `subscriptions` table is NOT in V1__init.sql** — JPA relies on ddl-auto:update to create it. Needs a V2 migration.
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

- **Architecture.md package root was wrong:** Was `com.restroqr.platform`, actual is `com.restaurantqr.platform`. Corrected in Phase 0.
- **Actual package structure is NOT flat:** Auth is under `modules.auth`, users are under top-level `users`. Architecture.md now reflects reality.
- **Security Package Structure**: Created DTO in `modules.auth.dto` rather than `platform.auth.dto` to maintain consistency with existing auth module structure.
- **Registration Flow**: Public self-registration now assigns `STAFF` role by default (vs previous insecure `RESTAURANT_OWNER`). Owner accounts must be created by Super Admin via separate secure flow.
- **Rate Limiting**: IMPLEMENTED via Bucket4j in `RateLimitFilter` (auth=20/min, public=120/min). Despite Memory.md previously listing this as PENDING — it is DONE.
- **Access Token Lifetime**: Currently 24h (86400000 ms). Architecture.md specifies ~15 min. This is a P0 security issue (P0-5 in INITIAL_AUDIT.md) — must be addressed in Phase 1 or Phase 10.

---

## Known Issues / Open TODOS
_(Things a future session would otherwise have to dig through code to find.)_

See `docs/audits/INITIAL_AUDIT.md` for the full 32-finding audit report. Key items:

- **P0-2 PAYMENT BYPASS**: `POST /subscriptions/restaurants/{id}/activate` has no payment verification — any OWNER can self-upgrade for free.
- **P0-5 TOKEN LIFETIME**: Access token is 24h (should be ~15 min per Architecture.md).
- **P1-1 BROKEN TESTS**: `AuthServiceTest` doesn't compile — `RegisterRequest` type mismatch after DTO refactor. Fix before any Phase 1 changes.
- **P1-3 UPLOAD OWNERSHIP**: `ImageUploadController` `uploadLogo`/`uploadBanner` don't verify restaurant ownership.
- **P1-5 BRANCH READ ISOLATION**: `BranchController.getById` does not verify branch belongs to the URL's `restaurantId`.
- **P1-6 CATEGORY READ ISOLATION**: `CategoryController.getById` same as above.
- **P1-8 SYSOUT IN PRODUCTION**: 7 `System.out.println` calls in `SecurityConfig` (Rules.md §3 violation).
- **P1-9 TEST ARTIFACT**: `controller/TestComponent.java` should be removed from production code.
- **P2-3 MISSING INDEX**: `reset_token` column in users table has no DB index.
- **P2-8 SUBSCRIPTIONS MISSING FROM FLYWAY**: `subscriptions` table not in V1__init.sql — needs V2 migration.
- **P2-10 @EnableJpaAuditing**: Verify it is present on main class (check if `createdAt`/`updatedAt` are populated).

---

## Next Session Should Start With
_(One or two lines telling the next session exactly where to pick up.)_

- **Phase 0 is COMPLETE.** Await user approval to begin Phase 1.
- Phase 1 priority order:
  1. Fix `AuthServiceTest` compile error (P1-1) — restore test coverage FIRST
  2. Reduce access token lifetime from 24h → 15min (P0-5)
  3. Fix upload ownership checks in `ImageUploadController` (P1-3)
  4. Fix cross-tenant read in `BranchController.getById` and `CategoryController.getById` (P1-5, P1-6)
  5. Remove `System.out.println` from `SecurityConfig` (P1-8)
  6. Add V2 Flyway migration for `subscriptions` table (P2-8)
  7. Address payment verification (P0-2) — discussion required

