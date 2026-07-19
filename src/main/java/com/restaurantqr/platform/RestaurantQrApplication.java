package com.restaurantqr.platform;

import com.restaurantqr.platform.users.entity.User;
import com.restaurantqr.platform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class RestaurantQrApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantQrApplication.class, args);
    }

    /**
     * Seeds the default Super Admin account on first boot.
     * Credentials: admin@restaurantqr.com / (password from env ADMIN_PASSWORD or generated)
     * IMPORTANT: Change this password immediately after first login!
     */
    @Bean
    CommandLineRunner seedSuperAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String superAdminEmail = "admin@restaurantqr.com";

            if (!userRepository.existsByEmailAndIsDeletedFalse(superAdminEmail)) {
                String envPassword = System.getenv("ADMIN_PASSWORD");
                String password;
                boolean generated = false;
                if (envPassword != null && !envPassword.isBlank()) {
                    password = envPassword;
                } else {
                    password = UUID.randomUUID().toString();
                    generated = true;
                }
                var admin = User.builder()
                        .name("Super Admin")
                        .email(superAdminEmail)
                        .password(passwordEncoder.encode(password))
                        .role(User.Role.SUPER_ADMIN)
                        .status(User.Status.ACTIVE)
                        .build();

                userRepository.save(admin);
                log.info("========================================================");
                log.info("  Super Admin seeded: {}", superAdminEmail);
                if (generated) {
                    log.info("  Generated password: {}", password);
                    log.info("  Please change the password after first login!");
                }
                log.info("========================================================");
            } else {
                log.info("Super Admin already exists — skipping seed.");
            }
        };
    }
}
