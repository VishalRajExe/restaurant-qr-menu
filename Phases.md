# Phases.md — Build Phases

Each phase should be completed, tested, and checked into `Memory.md` before moving to the next. The AI should not skip ahead.

---

## Phase 0 — Project Bootstrap + Production Readiness Audit ✅ COMPLETE

**Completed: 2026-07-20**

Original bootstrap tasks done (prior sessions):
- Spring Boot project initialized (Web, Security, Data JPA, Validation, MySQL driver, Flyway).
- Angular workspace created with two applications (public-menu and admin).
- `application.yml` set up for dev/staging/prod profiles with env-var placeholders.
- Flyway `V1__init.sql` baseline created.
- Global exception handler + standard `ApiResponse<T>` shape implemented.

Phase 0 Audit additions (this session):
- Full production-readiness audit completed (READ-ONLY).
- Architecture.md corrected: package root and structure verified against actual code.
- `docs/audits/API_INVENTORY.md` — 68 endpoints across 13 controllers catalogued.
- `docs/audits/INITIAL_AUDIT.md` — 32 findings (5 P0, 9 P1, 10 P2, 8 P3).
- Memory.md updated with all findings and corrections.

**Exit criteria:** ✅ App compiles (76 files). ⚠️ Test suite has 1 compile error (P1-1). All Phase 0 documentation deliverables produced.

---

## Phase 1 — Database Design & Security Foundation
**Status**: P0 CRITICAL SECURITY COMPLETE ✅

- Create Flyway migrations for: `restaurant`, `branch`, `category`, `menu_item`, `offers`, `qr_codes`, `users`.
- Add `created_at`, `updated_at`, `deleted_at` (soft delete) to every tenant table.
- Add foreign keys and indexes (`restaurant_id`, `branch_id`, `category_id`).
- Create JPA entities + repositories matching the schema.

**P0 Critical Security Fixes (COMPLETED):**
- [x] Fix P0-1: JWT secret fail-fast guard (`@PostConstruct` length validation ≥ 32 chars)
- [x] Fix P0-2: Subscription activation payment bypass (restricted direct activation to `SUPER_ADMIN` & enforced tenant scoping in service)
- [x] Fix P0-3 & P0-4: Information disclosure & tenant bypass on `GET /restaurants/{id}` (removed permitAll, fixed `/restaurants/slug/*` typo, enforced auth in `assertRestaurantAccess`)
- [x] Fix P0-5: JWT Access token expiration time reduced from 24h to 15m (`900000` ms)
- [x] Verify P0-6: Tenant isolation on `PUT /restaurants/{id}` verified and tested

**Exit criteria:** All P0 security issues resolved. App builds and passes 100% of unit & integration tests (`mvn clean test` = 15/15 SUCCESS). Detailed report produced in `docs/audits/SECURITY_AUDIT.md`.

---

## Phase 2 — Authentication, JWT and RBAC Module ✅ COMPLETE

**Completed: 2026-07-20**

- [x] JWT login (`/auth/login`), refresh token (`/auth/refresh`), forgot/reset password (`/auth/forgot-password`, `/auth/reset-password`), change password (`/auth/change-password`).
- [x] Password hashing using BCrypt (cost factor 12).
- [x] Role-based access control: `SUPER_ADMIN`, `RESTAURANT_OWNER`, `MANAGER`, `STAFF`.
- [x] Spring Security 401 Unauthorized (unauthenticated/expired/invalid token) vs 403 Forbidden (insufficient authority).
- [x] Token type validation: Access tokens (`type: ACCESS`, 15m) vs Refresh tokens (`type: REFRESH`, 7d).
- [x] Account status enforcement: Inactive/Suspended accounts rejected at filter level.
- [x] Public registration role escalation protection: Self-registration hardcoded to `STAFF`.

**Exit criteria:** ✅ Protected endpoints return 401 for unauthenticated/expired/invalid JWT requests; 403 for unauthorized roles; all 21 unit & integration tests pass (`mvn clean test` = 21/21 SUCCESS).

---

## Phase 3 — Multi-Tenant Security / IDOR / BOLA ✅ COMPLETE

**Completed: 2026-07-21**

- [x] Strict tenant isolation for Restaurant, Branch, Category, MenuItem, Offer, QrCode, User, and Analytics resources.
- [x] Enforced server-side `restaurantService.findById(restaurantId)` access assertion on every tenant-owned resource endpoint.
- [x] Hardened sub-resource lookup methods (`findById(id, restaurantId)`) against cross-tenant ID swapping.
- [x] Prevented cross-tenant relationship injection (e.g. creating MenuItem under Restaurant A with Category B ID).
- [x] Added automated regression test suite (`Phase3TenantIsolationTest`) covering GET, POST, PUT, DELETE attacks across all tenant entities.

**Exit criteria:** ✅ Tenant isolation verified end-to-end; Owner A cannot read/write/mutate Restaurant B's resources; all 33 unit & integration tests pass (`mvn clean test` = 33/33 SUCCESS).

---

## Phase 4 — Core Business Modules (Restaurant, Branch, Category, MenuItem, Offer) ✅ COMPLETE

**Completed: 2026-07-21**

- [x] Restaurant: CRUD, slug conflict handling (409 Conflict), tenant configuration.
- [x] Branch: CRUD, soft deletion (`isDeleted = true`), opening hours and coordinates.
- [x] Category: CRUD, display order reordering, active/inactive toggle, soft deletion.
- [x] MenuItem: CRUD, `BigDecimal` price handling with `@DecimalMin("0.01")` validation, category ownership scoping, availability toggle, search/filtering.
- [x] Offer: CRUD, date range validation (`startDate` <= `endDate`), discount type validation (`PERCENTAGE` 0.01–100%, `FLAT` > 0), active/expired logic.

**Exit criteria:** ✅ All 5 core business modules verified end-to-end; DTO validation, entity relationships, soft delete, conflict handling, and business rules enforced; all 38 unit & integration tests pass (`mvn clean test` = 38/38 SUCCESS).

---

## Phase 5 — QR Code Module
- QR generation endpoint using ZXing, encoding the public menu URL (with optional `table_number`).
- QR download (PNG) and print-friendly view.
- Angular: QR management page (generate, download, print per branch/table).

**Exit criteria:** Scanning a generated QR (real device test) opens the correct restaurant's public menu.

---

## Phase 6 — Public Customer Menu
- Public (unauthenticated) endpoints: get restaurant by slug, get menu (categories + items) by slug.
- Angular public menu app: home/banner, categories, item list, search, veg/non-veg + price filters, dark/light mode, multi-language.
- Mobile-responsive layout.

**Exit criteria:** Menu loads in < 2s on 4G test, search/filter work client-side or via API, dark/light toggle persists per session.

---

## Phase 7 — Offers Module
- Offers CRUD (title, description, discount, start/end date) scoped to restaurant.
- Display active offers on the public menu (date-range aware).
- Angular: odds management UI (admin) + offers banner (public menu).

**Exit criteria:** Only currently-active offers show on the public menu; expired/future offers are hidden automatically.

---

## Phase 8 — Analytics Module
- Scan-event logging (async, non-blocking) from the public menu.
- Aggregation endpoints: menu views, popular items, peak hours, device type.
- Angular admin dashboard: charts (Chart.js/ApexCharts) for the above.

**Exit criteria:** Dashboard reflects real scan data within an acceptable delay; aggregation queries are indexed and performant at expected scale.

---

## Phase 9 — Subscriptions & Billing
- Plan definitions (Basic/Professional/Enterprise) with enforced limits (branch count, item count).
- Razorpay + PayPal integration for recurring billing; webhook handlers for payment events.
- Super admin: revenue tracking, subscription status per restaurant.
- Enforce plan limits server-side (reject creating a 2nd branch on Basic plan, etc.).

**Exit criteria:** Plan limits are enforced with clear error messages; successful payment upgrades a restaurant's plan; webhook failures are logged and retried.

---

## Phase 10 — Security Hardening & QA
- Rate limiting on auth and public endpoints.
- Full input validation audit across all controllers.
- Audit log review, soft-delete verification.
- Penetration-style test pass: tenant isolation, JWT tampering, role escalation attempts.
- Load test public menu endpoints.

**Exit criteria:** No critical/high findings open; tenant isolation tests pass; load test meets target latency under expected concurrent scans.

---

## Phase 11 — Deployment
- Dockerize backend and frontend.
- CI/CD pipeline (build, test, migrate, deploy) for staging then production.
- Configure Nginx, TLS, environment variables, monitoring/logging.

**Exit criteria:** One-command (or one-pipeline) deploy to staging and production; health checks and logs accessible; rollback procedure documented.

---

## Suggested Timeline (single developer, from source PDF)
| Phase | Time |
|---|---|
| Database Design | 2 days |
| Backend APIs | 10 days |
| Admin Panel | 10 days |
| Customer Menu Website | 5 days |
| QR Generation | 1 day |
| Image Upload | 1 day |
| Analytics | 3 days |
| Testing | 5 days |
| Deployment | 2 days |
| **Total** | **~30–40 working days for MVP** |