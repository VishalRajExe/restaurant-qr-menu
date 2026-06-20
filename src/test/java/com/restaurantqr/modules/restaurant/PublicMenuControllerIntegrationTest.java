package com.restaurantqr.modules.restaurant;

import com.restaurantqr.RestaurantQrApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RestaurantQrApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicMenuControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /public/menu/invalid-token returns 404")
    void getMenuByToken_invalidToken_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/public/menu/invalid-token-xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /public/menu/restaurant/nonexistent-slug returns 404")
    void getMenuBySlug_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/public/menu/restaurant/nonexistent-slug-xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /auth/login with bad credentials returns 401")
    void login_badCredentials_returns401() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("""
                            {"email":"nobody@example.com","password":"wrongpassword"}
                            """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Protected endpoint without token returns 403")
    void protectedEndpoint_noToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isForbidden());
    }
}
