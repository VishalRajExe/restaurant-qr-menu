package com.restaurantqr.platform.modules.restaurant.controller;

import com.restaurantqr.platform.common.ApiResponse;
import com.restaurantqr.platform.analytics.service.AnalyticsService;
import com.restaurantqr.platform.modules.category.entity.Category;
import com.restaurantqr.platform.modules.category.service.CategoryService;
import com.restaurantqr.platform.modules.menuitem.entity.MenuItem;
import com.restaurantqr.platform.modules.menuitem.service.MenuItemService;
import com.restaurantqr.platform.modules.offer.entity.Offer;
import com.restaurantqr.platform.modules.offer.service.OfferService;
import com.restaurantqr.platform.modules.qr.entity.QrCode;
import com.restaurantqr.platform.modules.qr.service.QrCodeService;
import com.restaurantqr.platform.modules.restaurant.entity.Restaurant;
import com.restaurantqr.platform.modules.restaurant.service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/menu")
@RequiredArgsConstructor
public class PublicMenuController {

    private final QrCodeService qrCodeService;
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;
    private final MenuItemService menuItemService;
    private final OfferService offerService;
    private final AnalyticsService analyticsService;

    // ─── Resolve QR token → full restaurant menu.
    // GET /public/menu/{token}
    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<MenuPayload>> getMenuByToken(
            @PathVariable String token,
            HttpServletRequest request) {

        // 1. Resolve & validate QR code
        QrCode qrCode = qrCodeService.scan(token);
        Restaurant restaurant = qrCode.getRestaurant();
        Long restaurantId = restaurant.getId();

        // 3. Fetch menu data
        List<Category> categories = categoryService.findActiveByRestaurant(restaurantId);
        List<MenuItem> menuItems = menuItemService.getPublicMenu(restaurantId);
        List<Offer> activeOffers = offerService.getActiveOffers(restaurantId);

        // 4. Record scan event asynchronously (non-blocking)
        analyticsService.recordScan(qrCode, request);

        // 5. Build payload
        var payload = MenuPayload.builder()
                .restaurant(restaurant)
                .qrCode(qrCode)
                .categories(categories)
                .menuItems(menuItems)
                .activeOffers(activeOffers)
                .build();

        return ResponseEntity.ok(ApiResponse.success(payload));
    }

    // ─── Direct restaurant menu by slug (for shareable links like https://menu.yourdomain.com/r/winged-cafe)
    // GET /public/menu/restaurant/{slug}
    @GetMapping("/restaurant/{slug}")
    public ResponseEntity<ApiResponse<MenuPayload>> getMenuBySlug(@PathVariable String slug) {
        Restaurant restaurant = restaurantService.findBySlug(slug);
        if (restaurant.getStatus() != Restaurant.Status.ACTIVE) {
            throw new com.restaurantqr.platform.common.ResourceNotFoundException("Restaurant not found or inactive");
        }
        Long restaurantId = restaurant.getId();

        List<Category> categories = categoryService.findActiveByRestaurant(restaurantId);
        List<MenuItem> menuItems = menuItemService.getPublicMenu(restaurantId);
        List<Offer> activeOffers = offerService.getActiveOffers(restaurantId);

        var payload = MenuPayload.builder()
                .restaurant(restaurant)
                .qrCode(null)           // null when accessed via slug
                .categories(categories)
                .menuItems(menuItems)
                .activeOffers(activeOffers)
                .build();

        return ResponseEntity.ok(ApiResponse.success(payload));
    }

    @GetMapping("/restaurants/{restaurantId}/search")
    public ResponseEntity<ApiResponse<List<MenuItem>>> searchPublicMenu(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) MenuItem.FoodType vegNonveg) {
        analyticsService.recordSearch(restaurantId, q);
        return ResponseEntity.ok(ApiResponse.success(menuItemService.searchPublicMenu(restaurantId, q, vegNonveg)));
    }


    // ─── Response Payload
    @Data
    @Builder
    public static class MenuPayload {
        private Restaurant restaurant;
        private QrCode qrCode;           // null when accessed via slug
        private List<Category> categories;
        private List<MenuItem> menuItems;
        private List<Offer> activeOffers;
    }
}