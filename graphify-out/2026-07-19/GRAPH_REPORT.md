# Graph Report - .  (2026-07-19)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 843 nodes · 2095 edges · 20 communities (18 shown, 2 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 153 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9929464e`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- JwtTokenProvider
- User
- Offer
- .success
- Category
- Restaurant
- Subscription
- QrCode
- Branch
- SecurityConfig.java
- AnalyticsService
- ApiResponse
- SuperAdminController
- CloudinaryUploadService
- RestaurantQrApplication.java
- JwtUserDetails
- AppConfig.java
- CloudinaryConfig.java
- com.restaurantqr:restaurant-qr-backend
- README

## God Nodes (most connected - your core abstractions)
1. `ApiResponse` - 98 edges
2. `Restaurant` - 48 edges
3. `User` - 39 edges
4. `MenuItem` - 37 edges
5. `QrCode` - 36 edges
6. `RestaurantService` - 34 edges
7. `Category` - 33 edges
8. `Offer` - 32 edges
9. `Branch` - 28 edges
10. `Subscription` - 28 edges

## Surprising Connections (you probably didn't know these)
- `Category` --inherits--> `BaseEntity`  [EXTRACTED]
  src/main/java/com/restaurantqr/modules/category/entity/Category.java → src/main/java/com/restaurantqr/common/BaseEntity.java
- `MenuItem` --inherits--> `BaseEntity`  [EXTRACTED]
  src/main/java/com/restaurantqr/modules/menuitem/entity/MenuItem.java → src/main/java/com/restaurantqr/common/BaseEntity.java
- `Offer` --inherits--> `BaseEntity`  [EXTRACTED]
  src/main/java/com/restaurantqr/modules/offer/entity/Offer.java → src/main/java/com/restaurantqr/common/BaseEntity.java
- `QrCode` --inherits--> `BaseEntity`  [EXTRACTED]
  src/main/java/com/restaurantqr/modules/qr/entity/QrCode.java → src/main/java/com/restaurantqr/common/BaseEntity.java
- `Restaurant` --inherits--> `BaseEntity`  [EXTRACTED]
  src/main/java/com/restaurantqr/modules/restaurant/entity/Restaurant.java → src/main/java/com/restaurantqr/common/BaseEntity.java

## Import Cycles
- None detected.

## Communities (20 total, 2 thin omitted)

### Community 0 - "JwtTokenProvider"
Cohesion: 0.06
Nodes (43): BeforeEach, Claims, ExtendWith, JavaMailSender, SecretKey, BadRequestException, ResponseStatus, ResponseStatus (+35 more)

### Community 1 - "User"
Cohesion: 0.05
Nodes (52): ConflictException, ResponseStatus, ForbiddenException, ResponseStatus, PostMapping, DeleteMapping, GetMapping, Page (+44 more)

### Community 2 - "Offer"
Cohesion: 0.06
Nodes (47): JpaRepository, DeleteMapping, GetMapping, MultipartFile, PostMapping, PreAuthorize, PutMapping, RequiredArgsConstructor (+39 more)

### Community 3 - ".success"
Cohesion: 0.07
Nodes (42): DeleteMapping, GetMapping, Page, PatchMapping, PostMapping, PreAuthorize, PutMapping, RequiredArgsConstructor (+34 more)

### Community 4 - "Category"
Cohesion: 0.07
Nodes (37): Modifying, CategoryController, DeleteMapping, GetMapping, MultipartFile, PatchMapping, PostMapping, PreAuthorize (+29 more)

### Community 5 - "Restaurant"
Cohesion: 0.07
Nodes (37): DeleteMapping, GetMapping, Page, PostMapping, PreAuthorize, PutMapping, RequestMapping, RequiredArgsConstructor (+29 more)

### Community 6 - "Subscription"
Cohesion: 0.06
Nodes (42): SubscriptionPlan, BASIC, ENTERPRISE, PROFESSIONAL, GetMapping, PostMapping, PreAuthorize, RequestMapping (+34 more)

### Community 7 - "QrCode"
Cohesion: 0.07
Nodes (34): BufferedImage, DeleteMapping, GetMapping, PatchMapping, PostMapping, PreAuthorize, RequiredArgsConstructor, ResponseEntity (+26 more)

### Community 8 - "Branch"
Cohesion: 0.07
Nodes (37): EntityListeners, MappedSuperclass, BaseEntity, Getter, Setter, BranchController, DeleteMapping, GetMapping (+29 more)

### Community 9 - "SecurityConfig.java"
Cohesion: 0.09
Nodes (33): AuthenticationConfiguration, AuthenticationProvider, Bucket, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, HttpSecurity, OncePerRequestFilter (+25 more)

### Community 10 - "AnalyticsService"
Cohesion: 0.08
Nodes (31): Async, AnalyticsController, GetMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+23 more)

### Community 11 - "ApiResponse"
Cohesion: 0.16
Nodes (17): AccessDeniedException, BadCredentialsException, ExceptionHandler, JsonInclude, MaxUploadSizeExceededException, MethodArgumentNotValidException, MethodArgumentTypeMismatchException, RestControllerAdvice (+9 more)

### Community 12 - "SuperAdminController"
Cohesion: 0.15
Nodes (15): Scheduled, Component, RequiredArgsConstructor, Slf4j, Transactional, ScheduledTasks, GetMapping, Page (+7 more)

### Community 13 - "CloudinaryUploadService"
Cohesion: 0.18
Nodes (14): CloudinaryUploadService, Cloudinary, MultipartFile, RequiredArgsConstructor, Service, Slf4j, ImageUploadController, MultipartFile (+6 more)

### Community 14 - "RestaurantQrApplication.java"
Cohesion: 0.17
Nodes (14): ActiveProfiles, AutoConfigureMockMvc, CommandLineRunner, MockMvc, SpringBootApplication, SpringBootTest, Bean, PasswordEncoder (+6 more)

### Community 15 - "JwtUserDetails"
Cohesion: 0.15
Nodes (11): GrantedAuthority, CustomUserDetailsService, Override, RequiredArgsConstructor, Service, UserDetails, Getter, Override (+3 more)

### Community 16 - "AppConfig.java"
Cohesion: 0.39
Nodes (7): EnableAsync, EnableJpaAuditing, EnableScheduling, AppConfig, Bean, Configuration, WebMvcConfigurer

### Community 17 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

## Knowledge Gaps
- **43 isolated node(s):** `com.restaurantqr:restaurant-qr-backend`, `MOBILE`, `TABLET`, `DESKTOP`, `UNKNOWN` (+38 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ApiResponse` connect `ApiResponse` to `JwtTokenProvider`, `User`, `Offer`, `.success`, `Category`, `Restaurant`, `Subscription`, `QrCode`, `Branch`, `AnalyticsService`, `SuperAdminController`, `CloudinaryUploadService`?**
  _High betweenness centrality (0.246) - this node is a cross-community bridge._
- **Why does `Restaurant` connect `Restaurant` to `User`, `Offer`, `.success`, `Category`, `Subscription`, `QrCode`, `Branch`, `AnalyticsService`, `SuperAdminController`?**
  _High betweenness centrality (0.172) - this node is a cross-community bridge._
- **Why does `User` connect `User` to `JwtTokenProvider`, `Restaurant`, `Branch`, `SuperAdminController`, `JwtUserDetails`?**
  _High betweenness centrality (0.153) - this node is a cross-community bridge._
- **What connects `com.restaurantqr:restaurant-qr-backend`, `MOBILE`, `TABLET` to the rest of the system?**
  _43 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `JwtTokenProvider` be split into smaller, more focused modules?**
  _Cohesion score 0.05663474692202462 - nodes in this community are weakly interconnected._
- **Should `User` be split into smaller, more focused modules?**
  _Cohesion score 0.05194805194805195 - nodes in this community are weakly interconnected._
- **Should `Offer` be split into smaller, more focused modules?**
  _Cohesion score 0.05775638652350981 - nodes in this community are weakly interconnected._