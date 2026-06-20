# Restaurant QR Menu SaaS — Backend API

Spring Boot 3.2 · Java 17 · MySQL · JWT · Cloudinary · ZXing

---

## ⚠️ Fixes applied in this pass

This codebase was already largely built out, but had several bugs that would
have broken core flows once you started using it for real. Here's what changed:

1. **Password hashes were leaking in API responses.** `User.password`,
   `resetToken` and `resetTokenExpiry` had no `@JsonIgnore`, so every endpoint
   that returned a `User` (staff lists, super-admin user list, profile updates)
   sent the BCrypt hash to the browser. Fixed with `@JsonIgnore`.
2. **StackOverflowError waiting to happen.** `Subscription.restaurant` and
   `Restaurant.subscriptions` formed a circular JSON reference. The moment any
   restaurant got its first subscription (e.g. clicking "Upgrade" on the
   Subscription page), serializing that restaurant — or any category/menu
   item/user under it — would crash with a 500. Fixed by `@JsonIgnore`-ing the
   back-reference.
3. **"Create Restaurant + Owner" always failed.** `SuperAdminController.createOwner`
   forced the role to `RESTAURANT_OWNER`, then called a shared method that
   explicitly *rejects* that exact role. Onboarding a new restaurant was
   completely broken. Split into a dedicated `createOwnerAccount()` method.
4. **Customer-facing menu lookups were blocked by auth.** The security rule for
   `/restaurants/**` required an authenticated RESTAURANT_OWNER/MANAGER/SUPER_ADMIN
   for *every* method, including the read-only `GET /restaurants/{id}` and
   `GET /restaurants/slug/{slug}` the public menu page needs for anonymous
   customers. Added explicit `permitAll` rules for those GETs.
5. **STAFF accounts were locked out of the dashboard.** That same blanket rule
   didn't include the `STAFF` role, even though individual controller methods
   already allow it via `@PreAuthorize`. STAFF never got past the URL-level
   gate. Added STAFF to the rule.
6. **Generated QR codes pointed at a placeholder domain.** `qr.base-url`
   defaulted to `https://menu.yourdomain.com/menu`, so a freshly generated QR
   code wouldn't resolve anywhere on a local/dev setup. It now defaults to
   `${app.frontend-url}/menu` (`http://localhost:4200/menu` out of the box).
7. **Forgot Password didn't send an email.** It generated a reset token but
   only logged it to the console. Added a real `EmailService` using the
   `JavaMailSender` bean (SMTP creds were already configured) and wired it in.
8. **QR cards never showed which branch they belonged to.** `QrCode.branch`
   was `@JsonIgnore`d, so the frontend's `qr.branch?.name` was always blank.
   Exposed it (it's safe — `Branch.restaurant` is itself ignored, so no
   recursion risk).
9. Minor: rate limiter now runs before Spring Security (`@Order(HIGHEST_PRECEDENCE)`)
   so brute-force attempts on `/auth/**` get throttled before a password check
   even runs; added a clean 400 response for bad query-param types instead of
   a generic 500.

### 🔴 Rotate your credentials

`application.yml` had a **real Gmail app password and a real Cloudinary API
secret committed in plaintext**. Both still work as fallback defaults so
nothing breaks for you locally, but since this file may already have been
shared/pushed somewhere, treat both as compromised:

- Rotate the Gmail app password in your Google Account → Security → App
  passwords, then set it via `MAIL_PASSWORD` instead of editing the file.
- Rotate the Cloudinary API secret in your Cloudinary dashboard → Settings →
  Security, then set it via `CLOUDINARY_API_SECRET`.

---

## Quick Start

### 1. Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8.0+ running locally
- (Optional) Cloudinary account for image uploads

### 2. Configure
Copy `src/main/resources/application.yml` and set your values,
or export environment variables:

```bash
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your64charHexSecret
export CLOUDINARY_CLOUD_NAME=your_cloud
export CLOUDINARY_API_KEY=your_key
export CLOUDINARY_API_SECRET=your_secret
export QR_BASE_URL=https://menu.yourdomain.com/menu
export FRONTEND_URL=http://localhost:4200
export ADMIN_URL=http://localhost:4201
```

### 3. Run
```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080/api/v1`

**Default Super Admin credentials** (change immediately!):
- Email: `admin@restaurantqr.com`
- Password: `Admin@12345`

---

## Architecture

```
Customer scans QR
     ↓
GET /api/v1/public/menu/{token}
     ↓
Spring Boot resolves token → restaurant → full menu
     ↓
Analytics recorded async
     ↓
Response: { restaurant, categories, menuItems, activeOffers }
```

---

## API Reference

### Auth  `/auth/**`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/login` | Login, returns JWT tokens |
| POST | `/auth/register` | Self-register as Restaurant Owner |
| POST | `/auth/refresh` | Refresh access token |
| POST | `/auth/forgot-password` | Request password reset email |
| POST | `/auth/reset-password` | Reset with token |
| POST | `/auth/change-password` | Change while logged in |

### Public Menu  `/public/**` (no auth)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/public/menu/{token}` | Full menu by QR token (main endpoint) |
| GET | `/public/menu/restaurant/{slug}` | Full menu by restaurant slug |
| GET | `/public/restaurants/{id}/menu` | Menu items only |
| GET | `/public/restaurants/{id}/menu/search?q=&type=VEG` | Search menu |
| GET | `/public/restaurants/{id}/menu/featured` | Featured items |
| GET | `/public/restaurants/{id}/offers` | Active offers |
| GET | `/public/qr/{token}` | Resolve QR token (increments scan count) |

### Restaurants  (RESTAURANT_OWNER, SUPER_ADMIN)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/restaurants` | List all (SUPER_ADMIN only) |
| GET | `/restaurants/{id}` | Get by ID |
| GET | `/restaurants/slug/{slug}` | Get by slug |
| POST | `/restaurants` | Create (SUPER_ADMIN) |
| PUT | `/restaurants/{id}` | Update |
| DELETE | `/restaurants/{id}` | Soft delete (SUPER_ADMIN) |

### Branches  `/restaurants/{restaurantId}/branches`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List branches |
| GET | `/{id}` | Get branch |
| POST | `/` | Create (enforces subscription limit) |
| PUT | `/{id}` | Update |
| DELETE | `/{id}` | Soft delete |

### Categories  `/restaurants/{restaurantId}/categories`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/active` | Active categories (public) |
| GET | `/` | All categories |
| POST | `/` | Create |
| PUT | `/{id}` | Update |
| PUT | `/reorder` | Drag-and-drop reorder `[{id,displayOrder}]` |
| PATCH | `/{id}/toggle-status` | Toggle active/inactive |
| POST | `/{id}/image` | Upload category image |
| DELETE | `/{id}` | Soft delete |

### Menu Items  `/restaurants/{restaurantId}/menu-items`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/category/{categoryId}` | Items by category |
| POST | `/` | Create (enforces subscription limit) |
| PUT | `/{id}` | Update |
| PATCH | `/{id}/availability` | Toggle availability `{available: true}` |
| DELETE | `/{id}` | Soft delete |

### QR Codes  `/restaurants/{restaurantId}/qr-codes`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List all QR codes |
| POST | `/` | Generate QR (uploads PNG to Cloudinary) |
| PATCH | `/{id}/deactivate` | Deactivate |
| DELETE | `/{id}` | Soft delete |

### Offers  `/restaurants/{restaurantId}/offers`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List all offers |
| POST | `/` | Create |
| PUT | `/{id}` | Update |
| POST | `/{id}/banner` | Upload banner image |
| DELETE | `/{id}` | Soft delete |

### Analytics  `/analytics`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/restaurants/{restaurantId}/dashboard` | Today scans, monthly scans, device breakdown, top QRs |

### Subscriptions  `/subscriptions`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/plans` | Plan info + pricing |
| GET | `/restaurants/{id}/active` | Current active subscription |
| GET | `/restaurants/{id}/history` | Subscription history |
| POST | `/restaurants/{id}/activate` | Activate after payment |
| POST | `/restaurants/{id}/cancel` | Cancel subscription |

### Users  `/restaurants/{restaurantId}/users`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List staff |
| POST | `/` | Create MANAGER/STAFF account |
| PUT | `/{id}` | Update profile |
| PATCH | `/{id}/toggle-status` | Activate/deactivate |
| DELETE | `/{id}` | Remove |

### Image Upload  `/upload`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/menu-items/{restaurantId}/{itemId}` | Upload food image |
| POST | `/restaurants/{restaurantId}/logo` | Upload logo |
| POST | `/restaurants/{restaurantId}/banner` | Upload banner |

### Super Admin  `/super-admin`  (SUPER_ADMIN only)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/stats` | Platform-wide metrics |
| GET | `/restaurants` | All restaurants with search |
| PATCH | `/restaurants/{id}/status` | Suspend/activate |
| GET | `/users` | All users |
| POST | `/restaurants/{restaurantId}/owner` | Create owner account |
| GET | `/subscriptions/expiring-soon` | Expiring in 7 days |

---

## Roles & Permissions

| Role | Access |
|------|--------|
| `SUPER_ADMIN` | Everything |
| `RESTAURANT_OWNER` | Own restaurant + branches + users |
| `MANAGER` | Menu, categories, offers, QR |
| `STAFF` | View menu, toggle item availability |
| Public | `/public/**` and `/auth/**` |

---

## Subscription Plan Limits

| Plan | Branches | Menu Items | Price/month |
|------|----------|------------|-------------|
| BASIC | 1 | 100 | ₹999 |
| PROFESSIONAL | 5 | Unlimited | ₹2,999 |
| ENTERPRISE | Unlimited | Unlimited | ₹7,999 |

---

## Security

- JWT HS256, access token 24h, refresh token 7d
- BCrypt password hashing (strength 12)
- Rate limiting: 20 req/min on `/auth/**`, 120 req/min on `/public/**`
- Soft deletes on all entities (no hard data loss)
- Input validation on all request bodies
- CORS configured per `FRONTEND_URL` and `ADMIN_URL`

---

## Running Tests

```bash
mvn test
```

Uses H2 in-memory DB for tests — no MySQL needed.

---

## Project Structure

```
src/main/java/com/restaurantqr/
├── RestaurantQrApplication.java        # Main class + super admin seed
├── common/                             # Base entity, ApiResponse, exceptions, global handler
├── config/                             # Security, Cloudinary, app config, rate limiter, scheduler
├── security/                           # JWT provider, filter, UserDetails
└── modules/
    ├── auth/                           # Login, register, password reset
    ├── restaurant/                     # Restaurant CRUD, super admin, public menu
    ├── branch/                         # Branch management
    ├── category/                       # Categories + reordering
    ├── menuitem/                       # Menu items + image upload
    ├── offer/                          # Offers/deals
    ├── qr/                             # QR generation (ZXing + Cloudinary)
    ├── analytics/                      # Scan tracking + dashboard stats
    ├── subscription/                   # Plans + lifecycle
    └── user/                           # User/staff management
```
