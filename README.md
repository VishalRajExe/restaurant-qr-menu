# 🍽️ Restaurant QR Menu Platform — Backend API

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue.svg)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20H2-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

An enterprise-grade, multi-tenant **Restaurant QR Menu SaaS Backend** built with **Java 17** and **Spring Boot 3.2**. The platform enables multi-branch restaurant operations, automated QR code generation, category/item menu management, active promotion offers, tier subscription management, automated email reminders, and image storage via Cloudinary.

---

## 🌟 Key Features

### 🏢 Multi-Tenant & Multi-Branch Architecture
- **Logical Data Isolation**: Every tenant entity is scoped by `restaurant_id` and `branch_id`.
- **Role-Based Access Control (RBAC)**: Support for `SUPER_ADMIN`, `RESTAURANT_ADMIN`, and `STAFF` roles.
- **Super-Admin Management**: Centralized onboarding and subscription tier management for all registered restaurants.

### 🔐 Authentication & Security
- **Stateless Authentication**: JWT access tokens (15-min expiration) and refresh tokens (7-day sliding expiration).
- **Password Security**: BCrypt password hashing and secure token-based password reset workflow.
- **Rate Limiting**: Integrated **Bucket4j** filter protection against brute force and Denial of Service (DoS) attacks on sensitive endpoints.

### 📱 Dynamic QR Code Generation
- **ZXing QR Engine**: Dynamic generation of high-resolution QR codes in PNG or Data URI format.
- **Table & Branch Tracking**: Unique QR tokens for public menu access with table-specific metadata for scan analytics.

### 🖼️ Image Storage & Media Uploads
- **Cloudinary Storage**: Automated media upload and CDN hosting for dish images, restaurant logos, and promotional banners.

### 📧 Automated Email & SMTP Reminders
- **Spring Mail Integration**: Enterprise email dispatch for subscription expiry alerts, welcome messages, and password recovery.
- **Multi-Provider Support**: Pre-configured templates for Gmail SMTP, Mailtrap, SendGrid, Amazon SES, and local Mailpit/Mailhog.

### 🏷️ Menu, Category & Offer Management
- **Hierarchical Categories**: Re-orderable menu categories with status toggling (`ACTIVE`, `INACTIVE`).
- **Rich Menu Items**: Price, diet indicators (`VEG`, `NON_VEG`, `EGG`), allergens, tags, and Cloudinary media links.
- **Promotional Offers**: Percentage/fixed discount codes with validity periods and branch-level activation.

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Framework** | Spring Boot 3.2.3 (Java 17) |
| **Security** | Spring Security 6, JWT (`jjwt` 0.11.5), Bucket4j |
| **Data Access** | Spring Data JPA, Hibernate, MySQL 8 / H2 |
| **Database Migration** | Flyway |
| **QR Code Engine** | Google ZXing 3.5.2 |
| **Media Uploads** | Cloudinary Java SDK 1.36.0 |
| **Utilities** | Lombok, MapStruct |
| **API Testing** | Postman & Thunder Client Collections |

---

## 📁 Project Structure

```
com.restaurantqr.platform
├── RestaurantQrApplication.java       # Main Application Entry Point
├── config/                             # Security, Cloudinary, Mail, Rate Limiting & Async Config
├── security/                           # JwtAuthenticationFilter, JwtTokenProvider, JwtUserDetails
├── common/                             # BaseEntity, GlobalExceptionHandler, ApiResponse Wrapper
├── modules/
│   ├── auth/                           # Login, Registration, Token Refresh, Password Recovery
│   ├── restaurant/                     # Super Admin management, Restaurant profiles & Public Menu API
│   ├── branch/                         # Branch location management
│   ├── category/                       # Menu Category CRUD & Ordering
│   ├── menuitem/                       # Menu Item CRUD, Dish Type & Cloudinary Image Upload
│   ├── offer/                          # Discounts & Promotion Campaigns
│   ├── qr/                             # ZXing QR Generator & Metadata
│   └── subscription/                   # Tier Plans & Expiry Engine
└── users/                              # User entity, roles & staff account management
```

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK 17+)**
- **Apache Maven 3.8+**
- **MySQL 8.0+** (or use in-memory H2 for rapid development)

### 1. Clone the Repository
```bash
git clone https://github.com/VishalRajExe/restaurant-qr-menu.git
cd restaurant-qr-menu
```

### 2. Environment Configuration
Copy the provided `.env.example` template to create your local `.env` configuration or set the environment variables in your IDE:

```bash
cp .env.example .env
```

Key environment variables:
```properties
PORT=8080
DB_URL=jdbc:mysql://localhost:3306/restaurant_qr_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_password

JWT_SECRET=7A25432A462D4A614E645267556B58703273357638792F423F4528482B4D6251

MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=your_mailtrap_user
MAIL_PASSWORD=your_mailtrap_password

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

### 3. Build & Run
Compile the project and start the Spring Boot application:

```bash
# Clean and compile
mvn clean install

# Run application locally
mvn spring-boot:run
```
The backend API server will start on `http://localhost:8080/api/v1`.

---

## 📡 API Documentation & Collections

The workspace includes complete pre-configured collections for both **Postman** and **Thunder Client**:

- **Postman Collection**: `postman/Restaurant_QR_Menu_API.postman_collection.json`
- **Postman Environment**: `postman/Restaurant_QR_Menu_Env.postman_environment.json`
- **Thunder Client Collection**: `thunder-client/thunder-collection_Restaurant_QR_Menu_API.json`
- **Thunder Client Environment**: `thunder-client/thunder-environment_Restaurant_QR_Menu_Env.json`

### Key Base Endpoints Overview

| Area | HTTP Method | Endpoint | Access Level |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/v1/auth/login` | Public |
| **Auth** | `POST` | `/api/v1/auth/refresh` | Public |
| **Public Menu** | `GET` | `/api/v1/public/menu/{slug}` | Public |
| **Super Admin** | `GET` | `/api/v1/super-admin/restaurants` | `SUPER_ADMIN` |
| **Restaurants** | `GET / PUT` | `/api/v1/restaurants/{id}` | `RESTAURANT_ADMIN` |
| **Branches** | `GET / POST` | `/api/v1/branches` | `RESTAURANT_ADMIN` |
| **Categories** | `GET / POST` | `/api/v1/categories` | Admin / Staff |
| **Menu Items** | `GET / POST` | `/api/v1/menu-items` | Admin / Staff |
| **Media Upload**| `POST` | `/api/v1/menu-items/upload-image` | Admin / Staff |
| **QR Code** | `GET` | `/api/v1/qr-codes/branch/{branchId}/image` | Admin / Staff |
| **Offers** | `GET / POST` | `/api/v1/offers` | Admin |
| **Subscription**| `GET` | `/api/v1/subscriptions/current` | Admin |

---

## 🔒 Security Best Practices

- **Zero Hardcoded Secrets**: Production deployments pull secrets directly from environment variables.
- **Sanitized Repositories**: Local configuration templates contain placeholder secrets to prevent secret leaks.
- **Gitignore Protection**: `.env`, IDE artifacts (`.idea`, `.vscode`, `.classpath`), and target build outputs (`target/`) are strictly excluded from version control.

---

## 📜 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
