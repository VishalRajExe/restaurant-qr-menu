# Phases.md — Build Phases

Each phase should be completed, tested, and checked into `Memory.md` before moving to the next. The AI should not skip ahead.

---

## Phase 0 — Project Bootstrap
- Initialize Spring Boot project (Web, Security, Data JPA, Validation, MySQL driver, Flyway).
- Initialize Angular workspace (admin app + public menu app, or one app with role-gated routes).
- Set up `application.yml` for dev/staging/prod profiles with env-var placeholders.
- Set up base folder structure exactly as in `Architecture.md`.
- Set up Flyway `V1__init.sql` baseline.
- Set up global exception handler skeleton and standard API error shape.

**Exit criteria:** App boots, connects to MySQL, `/actuator/health` (or a basic ping endpoint) returns 200. Angular app builds and serves a blank shell.

---

## Phase 1 — Database Design & Security Foundation
**Status**: IN PROGRESS (Focusing on P0 Critical Security)

- Create Flyway migrations for: `restaurant`, `branch`, `category`, `menu_item`, `offers`, `qr_codes`, `users`.
- Add `created_at`, `updated_at`, `deleted_at` (soft delete) to every tenant table.
- Add foreign keys and indexes (`restaurant_id`, `branch_id`, `category_id`).
- Create JPA entities + repositories matching the schema.

**Additional Security Focus (P0 Critical):**
- [ ] Fix critical vulnerability: Public self-registration allows privilege escalation to RESTAURANT_OWNER
- [ ] Fix security issue: Remove default value for Cloudinary API key in application.yml
- [ ] Implement comprehensive input validation across all endpoints
- [ ] Add security headers (CSP, HSTS, etc.) via web filter
- [ ] Implement rate limiting on authentication endpoints

**Exit criteria:** All tables created via migration (not manual SQL), entities map cleanly, repositories pass basic save/find tests. All P0 security issues resolved. Application builds, starts, and passes basic security validation.

---

## Phase 2 — Auth Module
- JWT login, logout, refresh token endpoints.
- Password hashing (BCrypt), forgot/reset password flow.
- Role-based access: `SUPER_ADMIN`, `RESTAURANT_OWNER`, `MANAGER`, `STAFF`.
- Spring Security config + JWT filter + method-level `@PreAuthorize`.
- Angular: login page, auth guard, JWT interceptor, refresh-token handling.

**Exit criteria:** Can register/login as each role; protected endpoints reject unauthenticated/unauthorized requests; refresh token flow works end-to-end.

---

## Phase 3 — Restaurant & Branch Management
- Restaurant CRUD (super admin creates restaurants; owner edits own profile).
- Branch CRUD scoped to `restaurant_id`.
- Angular: restaurant profile page, branch management page.

**Exit criteria:** Owner can view/edit only their own restaurant; super admin can manage all restaurants; tenant isolation verified with a test (owner A cannot read/write restaurant B's data).

---

## Phase 4 — Category & Menu Item Management
- Category CRUD, display order (drag-and-drop support in API via an `order` field).
- Menu item CRUD, image upload (to S3/Cloudinary, store `image_url` only), veg/non-veg flag, availability toggle.
- Bulk upload endpoint (CSV or batch JSON) for menu items.
- Angular: categories CRUD UI with drag-and-drop, menu items CRUD UI with image upload and bulk upload.

**Exit criteria:** Full CRUD works, images upload and render correctly, ordering persists, bulk upload validates and reports row-level errors.

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