package org.icd4.commerce.adapter.webapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.icd4.commerce.adapter.webapi.dto.StockRegisterRequest;
import org.icd4.commerce.adapter.webapi.dto.StockUpdateRequest;
import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest

@org.springframework.context.annotation.Import(org.icd4.commerce.config.TestConfig.class)
@Transactional
class StockApiTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("재고 등록 API - 성공")
    void registerStock_Success() throws Exception {
        // Given
        StockRegisterRequest request = new StockRegisterRequest("test-product-123", 100L);

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 등록되었습니다."))
                .andExpect(jsonPath("$.data.productId").value("test-product-123"))
                .andExpect(jsonPath("$.data.quantity").value(100))
                .andExpect(jsonPath("$.data.stockStatus").value("AVAILABLE"));
    }

    @Test
    @DisplayName("재고 등록 API - 실패 (유효성 검증 - 수량 0)")
    void registerStock_Fail_ZeroQuantity() throws Exception {
        // Given
        StockRegisterRequest request = new StockRegisterRequest("test-product-123", 0L);

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("재고 등록 API - 실패 (유효성 검증 - 음수 수량)")
    void registerStock_Fail_NegativeQuantity() throws Exception {
        // Given
        StockRegisterRequest request = new StockRegisterRequest("test-product-123", -10L);

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("재고 등록 API - 실패 (상품 ID 누락)")
    void registerStock_Fail_MissingProductId() throws Exception {
        // Given
        StockRegisterRequest request = new StockRegisterRequest(null, 100L);

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("상품 ID는 필수 입니다."));
    }

    @Test
    @DisplayName("재고 조회 API - 성공")
    void getStock_Success() throws Exception {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-456", 200L));

        // When & Then
        mockMvc.perform(get("/api/stocks/{stockId}", savedStock.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고 조회 성공"))
                .andExpect(jsonPath("$.data.stockId").value(savedStock.getId()))
                .andExpect(jsonPath("$.data.productId").value("test-product-456"))
                .andExpect(jsonPath("$.data.quantity").value(200))
                .andExpect(jsonPath("$.data.stockStatus").value("AVAILABLE"));
    }

    @Test
    @DisplayName("재고 조회 API - 실패 (존재하지 않는 재고)")
    void getStock_Fail_NotFound() throws Exception {
        // Given
        String nonExistentStockId = "non-existent-stock-id";

        // When & Then
        mockMvc.perform(get("/api/stocks/{stockId}", nonExistentStockId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("재고를 찾을 수 없습니다")));
    }

    @Test
    @DisplayName("재고 수량 조회 API - 성공")
    void getStockQuantity_Success() throws Exception {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-789", 300L));

        // When & Then
        mockMvc.perform(get("/api/stocks/{stockId}/quantity", savedStock.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고 수량 조회 성공"))
                .andExpect(jsonPath("$.data").value(300));
    }

    @Test
    @DisplayName("재고 증가 API - 성공")
    void increaseStock_Success() throws Exception {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-111", 50L));
        StockUpdateRequest request = new StockUpdateRequest(30L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/{stockId}/increase", savedStock.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 증가되었습니다."));

        // 실제로 재고가 증가했는지 확인
        mockMvc.perform(get("/api/stocks/{stockId}/quantity", savedStock.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(80)); // 50 + 30 = 80
    }

    @Test
    @DisplayName("재고 감소 API - 성공")
    void decreaseStock_Success() throws Exception {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-222", 100L));
        StockUpdateRequest request = new StockUpdateRequest(20L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/{stockId}/decrease", savedStock.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 감소되었습니다."));

        // 실제로 재고가 감소했는지 확인
        mockMvc.perform(get("/api/stocks/{stockId}/quantity", savedStock.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(80)); // 100 - 20 = 80
    }

    @Test
    @DisplayName("재고 감소 API - 실패 (재고 부족)")
    void decreaseStock_Fail_InsufficientStock() throws Exception {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-333", 50L));
        StockUpdateRequest request = new StockUpdateRequest(100L); // 50개보다 많이 감소 시도

        // When & Then
        mockMvc.perform(patch("/api/stocks/{stockId}/decrease", savedStock.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("현재 재고보다 많습니다")));
    }

    @Test
    @DisplayName("재고 업데이트 API - 실패 (유효성 검증 - 수량 0)")
    void updateStock_Fail_ZeroQuantity() throws Exception {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-444", 50L));
        StockUpdateRequest request = new StockUpdateRequest(0L);

        // When & Then
        mockMvc.perform(patch("/api/stocks/{stockId}/increase", savedStock.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("재고는 0 이하의 값이 될 수 없습니다."));
    }
} 