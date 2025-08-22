package org.icd4.commerce.adapter.webapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.icd4.commerce.adapter.webapi.dto.StockUpdateRequest;
import org.icd4.commerce.application.StockService;
import org.icd4.commerce.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockApi.class)
class StockApiV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockService stockService;

    @Test
    @DisplayName("POST /api/stocks/v2 - Redis 재고 등록 성공")
    void registerStockV2_Success() throws Exception {
        // Given
        String productId = "test-product-123";
        Long quantity = 100L;
        Stock registeredStock = Stock.register(productId, quantity);
        
        when(stockService.registerV2(eq(productId), eq(quantity)))
                .thenReturn(registeredStock);

        // When & Then
        mockMvc.perform(post("/api/stocks/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":\"" + productId + "\",\"quantity\":" + quantity + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 등록되었습니다. (Redis)"))
                .andExpect(jsonPath("$.data.stockId").value(registeredStock.getId()))
                .andExpect(jsonPath("$.data.productId").value(productId))
                .andExpect(jsonPath("$.data.quantity").value(quantity));
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/increase - 성공")
    void increaseStockV2_Success() throws Exception {
        // Given
        String stockId = "test-stock-123";
        Long increaseQuantity = 50L;
        Long expectedResult = 150L;
        
        StockUpdateRequest request = new StockUpdateRequest(increaseQuantity);
        
        when(stockService.increaseQuantityV2(eq(stockId), eq(increaseQuantity)))
                .thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 증가되었습니다. (Redis)"))
                .andExpect(jsonPath("$.data").value(expectedResult));
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/increase - 재고가 존재하지 않는 경우")
    void increaseStockV2_StockNotFound() throws Exception {
        // Given
        String stockId = "non-existent-stock";
        Long increaseQuantity = 50L;
        
        StockUpdateRequest request = new StockUpdateRequest(increaseQuantity);
        
        when(stockService.increaseQuantityV2(eq(stockId), eq(increaseQuantity)))
                .thenThrow(new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId));

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/increase - 잘못된 요청 데이터")
    void increaseStockV2_InvalidRequest() throws Exception {
        // Given
        String stockId = "test-stock-123";
        String invalidJson = "{\"quantity\": \"invalid\"}";

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/decrease - 성공")
    void decreaseStockV2_Success() throws Exception {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = 30L;
        Long expectedResult = 70L;
        
        StockUpdateRequest request = new StockUpdateRequest(decreaseQuantity);
        
        when(stockService.decreaseQuantityV2(eq(stockId), eq(decreaseQuantity)))
                .thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 성공적으로 감소되었습니다. (Redis)"))
                .andExpect(jsonPath("$.data").value(expectedResult));
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/decrease - 재고가 존재하지 않는 경우")
    void decreaseStockV2_StockNotFound() throws Exception {
        // Given
        String stockId = "non-existent-stock";
        Long decreaseQuantity = 30L;
        
        StockUpdateRequest request = new StockUpdateRequest(decreaseQuantity);
        
        when(stockService.decreaseQuantityV2(eq(stockId), eq(decreaseQuantity)))
                .thenThrow(new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId));

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/decrease - 재고 수량이 부족한 경우")
    void decreaseStockV2_InsufficientQuantity() throws Exception {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = 100L;
        
        StockUpdateRequest request = new StockUpdateRequest(decreaseQuantity);
        
        when(stockService.decreaseQuantityV2(eq(stockId), eq(decreaseQuantity)))
                .thenThrow(new IllegalArgumentException("재고 수량이 부족합니다. stockId: " + stockId));

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/decrease - 잘못된 요청 데이터")
    void decreaseStockV2_InvalidRequest() throws Exception {
        // Given
        String stockId = "test-stock-123";
        String invalidJson = "{\"quantity\": \"invalid\"}";

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/increase - 음수 수량")
    void increaseStockV2_NegativeQuantity() throws Exception {
        // Given
        String stockId = "test-stock-123";
        Long negativeQuantity = -10L;
        
        StockUpdateRequest request = new StockUpdateRequest(negativeQuantity);
        
        when(stockService.increaseQuantityV2(eq(stockId), eq(negativeQuantity)))
                .thenThrow(new IllegalArgumentException("수량은 0 이상이어야 합니다."));

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/decrease - 음수 수량")
    void decreaseStockV2_NegativeQuantity() throws Exception {
        // Given
        String stockId = "test-stock-123";
        Long negativeQuantity = -10L;
        
        StockUpdateRequest request = new StockUpdateRequest(negativeQuantity);
        
        when(stockService.decreaseQuantityV2(eq(stockId), eq(negativeQuantity)))
                .thenThrow(new IllegalArgumentException("수량은 0 이상이어야 합니다."));

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/increase - 0으로 증가")
    void increaseStockV2_ZeroQuantity() throws Exception {
        // Given
        String stockId = "test-stock-123";
        Long zeroQuantity = 0L;
        Long expectedResult = 100L;
        
        StockUpdateRequest request = new StockUpdateRequest(zeroQuantity);
        
        when(stockService.increaseQuantityV2(eq(stockId), eq(zeroQuantity)))
                .thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/increase", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedResult));
    }

    @Test
    @DisplayName("PATCH /api/stocks/v2/{stockId}/decrease - 0으로 감소")
    void decreaseStockV2_ZeroQuantity() throws Exception {
        // Given
        String stockId = "test-stock-123";
        Long zeroQuantity = 0L;
        Long expectedResult = 100L;
        
        StockUpdateRequest request = new StockUpdateRequest(zeroQuantity);
        
        when(stockService.decreaseQuantityV2(eq(stockId), eq(zeroQuantity)))
                .thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(patch("/api/stocks/v2/{stockId}/decrease", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedResult));
    }
} 