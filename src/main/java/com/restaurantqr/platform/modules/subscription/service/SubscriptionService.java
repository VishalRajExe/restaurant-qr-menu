package com.restaurantqr.platform.modules.subscription.service;

import com.restaurantqr.platform.common.BadRequestException;
import com.restaurantqr.platform.common.ResourceNotFoundException;
import com.restaurantqr.platform.modules.restaurant.entity.Restaurant;
import com.restaurantqr.platform.modules.restaurant.repository.RestaurantRepository;
import com.restaurantqr.platform.modules.subscription.entity.Subscription;
import com.restaurantqr.platform.modules.subscription.repository.SubscriptionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final RestaurantRepository restaurantRepository;
    private final com.restaurantqr.platform.modules.restaurant.service.RestaurantService restaurantService;

    // Plan pricing (INR / month)
    public static final BigDecimal BASIC_PRICE        = new BigDecimal("999");
    public static final BigDecimal PROFESSIONAL_PRICE = new BigDecimal("2999");
    public static final BigDecimal ENTERPRISE_PRICE   = new BigDecimal("7999");

    public Optional<Subscription> getActiveSubscription(Long restaurantId) {
        restaurantService.findById(restaurantId);
        return subscriptionRepository.findActiveSubscription(restaurantId, LocalDate.now());
    }

    public List<Subscription> getHistory(Long restaurantId) {
        restaurantService.findById(restaurantId);
        return subscriptionRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Called after successful payment gateway webhook.
     * Creates subscription record and upgrades restaurant plan.
     */
    @Transactional
    public Subscription activate(Long restaurantId, SubscriptionRequest request) {
        var restaurant = restaurantService.findById(restaurantId);

        // Expire current active sub if any
        subscriptionRepository.findActiveSubscription(restaurantId, LocalDate.now())
                .ifPresent(sub -> {
                    sub.setStatus(Subscription.Status.EXPIRED);
                    subscriptionRepository.save(sub);
                });

        int months = request.months != null ? request.months : 1;
        BigDecimal amount = getPlanPrice(request.plan).multiply(BigDecimal.valueOf(months));

        var subscription = Subscription.builder()
                .restaurant(restaurant)
                .plan(request.plan)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(months))
                .amountPaid(amount)
                .paymentId(request.paymentId)
                .paymentGateway(request.paymentGateway)
                .status(Subscription.Status.ACTIVE)
                .build();

        subscriptionRepository.save(subscription);

        // Sync plan to restaurant
        restaurant.setSubscriptionPlan(mapToRestaurantPlan(request.plan));
        restaurantRepository.save(restaurant);

        log.info("Subscription activated: restaurant={} plan={} until={}",
                restaurantId, request.plan, subscription.getEndDate());

        return subscription;
    }

    @Transactional
    public void cancel(Long restaurantId) {
        restaurantService.findById(restaurantId);
        var sub = subscriptionRepository.findActiveSubscription(restaurantId, LocalDate.now())
                .orElseThrow(() -> new BadRequestException("No active subscription found"));
        sub.setStatus(Subscription.Status.CANCELLED);
        subscriptionRepository.save(sub);
        log.info("Subscription cancelled for restaurant={}", restaurantId);
    }

    /** Returns pricing info for the frontend plan selector */
    public PlanDetails getPlanDetails() {
        return new PlanDetails(
                new PlanInfo("BASIC", BASIC_PRICE, "1 branch", "100 menu items", "QR generation", "Analytics"),
                new PlanInfo("PROFESSIONAL", PROFESSIONAL_PRICE, "5 branches", "Unlimited items", "Priority support", "Custom branding"),
                new PlanInfo("ENTERPRISE", ENTERPRISE_PRICE, "Unlimited branches", "Unlimited items", "Dedicated support", "White labelling")
        );
    }

    private BigDecimal getPlanPrice(Subscription.Plan plan) {
        return switch (plan) {
            case BASIC        -> BASIC_PRICE;
            case PROFESSIONAL -> PROFESSIONAL_PRICE;
            case ENTERPRISE   -> ENTERPRISE_PRICE;
        };
    }

    private Restaurant.SubscriptionPlan mapToRestaurantPlan(Subscription.Plan plan) {
        return switch (plan) {
            case BASIC        -> Restaurant.SubscriptionPlan.BASIC;
            case PROFESSIONAL -> Restaurant.SubscriptionPlan.PROFESSIONAL;
            case ENTERPRISE   -> Restaurant.SubscriptionPlan.ENTERPRISE;
        };
    }

    // ─── DTOs ─────────────────────────────────────────────────────────────────

    public record PlanDetails(PlanInfo basic, PlanInfo professional, PlanInfo enterprise) {}
    public record PlanInfo(String name, BigDecimal pricePerMonth, String... features) {}
}
