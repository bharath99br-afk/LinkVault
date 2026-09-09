package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;

@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class IntegrationTestBase {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    offer_card_applicability,
                    offer_bank_applicability,
                    links,
                    products,
                    merchants,
                    cards,
                    offers,
                    global_merchants,
                    banks,
                    users
                RESTART IDENTITY CASCADE
                """);
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@example.com";
    }

    protected String registerAndLogin(String name, String email)
            throws Exception {

        String password = "Password@123";

        String registerJson = """
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(name, email, password);

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated());

        String loginJson = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        MvcResult result = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root =
                objectMapper.readTree(result.getResponse().getContentAsString());

        return root.path("data").path("token").asText();
    }

    protected String auth(String token) {
        return "Bearer " + token;
    }

    protected long extractId(MvcResult result) throws Exception {
        JsonNode root =
                objectMapper.readTree(result.getResponse().getContentAsString());

        return root.path("data").path("id").asLong();
    }

    protected long createBank(String token, String name)
            throws Exception {

        String json = """
                {
                    "name": "%s"
                }
                """.formatted(name);

        MvcResult result = mockMvc.perform(
                post("/api/banks")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

    protected long createCard(
            String token,
            String name,
            long bankId)
            throws Exception {

        String json = """
                {
                    "name": "%s",
                    "lastFourDigits": "1234",
                    "cardType": "CREDIT",
                    "bankId": %d
                }
                """.formatted(name, bankId);

        MvcResult result = mockMvc.perform(
                post("/api/cards")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

    protected long createGlobalMerchant(
            String token,
            String name)
            throws Exception {

        String json = """
                {
                    "name": "%s",
                    "websiteUrl": "https://%s.example.com"
                }
                """.formatted(name, name.toLowerCase().replace(" ", ""));

        MvcResult result = mockMvc.perform(
                post("/api/global-merchants")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

    protected long createMerchant(
            String token,
            String name,
            Long globalMerchantId)
            throws Exception {

        String globalMerchantPart =
                globalMerchantId == null
                        ? ""
                        : ", \"globalMerchantId\": " + globalMerchantId;

        String json = """
                {
                    "name": "%s",
                    "websiteUrl": "https://%s.example.com"%s
                }
                """.formatted(
                        name,
                        name.toLowerCase().replace(" ", ""),
                        globalMerchantPart);

        MvcResult result = mockMvc.perform(
                post("/api/merchants")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

    protected long createProduct(
            String token,
            String name,
            long merchantId)
            throws Exception {

        String json = """
                {
                    "name": "%s",
                    "description": "Integration test product",
                    "category": "Electronics",
                    "websiteUrl": "https://product.example.com",
                    "merchantId": %d
                }
                """.formatted(name, merchantId);

        MvcResult result = mockMvc.perform(
                post("/api/products")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

protected long createLink(
        String token,
        String title,
        Long merchantId,
        Long productId)
        throws Exception {

    StringBuilder json = new StringBuilder("""
            {
                "title": "%s",
                "url": "https://example.com/product"
            """.formatted(title));

    if (merchantId != null) {
        json.append("""
                ,
                "merchantId": %d
                """.formatted(merchantId));
    }

    if (productId != null) {
        json.append("""
                ,
                "productId": %d
                """.formatted(productId));
    }

    json.append("}");

    MvcResult result = mockMvc.perform(
            post("/api/links")
                    .header("Authorization", auth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
            .andExpect(status().isCreated())
            .andReturn();

    return extractId(result);
}

    protected long createOffer(
            String token,
            String title,
            BigDecimal discountValue,
            Long globalMerchantId)
            throws Exception {

        String merchantPart =
                globalMerchantId == null
                        ? ""
                        : ", \"globalMerchantId\": " + globalMerchantId;

        String json = """
                {
                    "title": "%s",
                    "description": "Integration test offer",
                    "discountType": "PERCENTAGE",
                    "discountValue": %s,
                    "startDate": "%s",
                    "endDate": "%s"%s
                }
                """.formatted(
                        title,
                        discountValue.toPlainString(),
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(30),
                        merchantPart);

        MvcResult result = mockMvc.perform(
                post("/api/offers")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }
}