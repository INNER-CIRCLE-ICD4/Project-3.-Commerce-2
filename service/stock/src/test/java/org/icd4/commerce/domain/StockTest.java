package org.icd4.commerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    @DisplayName("재고 등록 - 성공")
    void register_Success() {
        String productId = "testProductId";
        Long quantity = 100L;

        Stock stock = Stock.register(productId, quantity);

        assertNotNull(stock.getSku());
        assertEquals(productId, stock.getSku());
        assertEquals(quantity, stock.getQuantity());
        assertEquals(stock.getStockStatus(), StockStatus.AVAILABLE);
        assertNotNull(stock.getCreatedAt());
        assertNotNull(stock.getUpdatedAt());
    }

    @Test
    @DisplayName("재고 등록 - 실패 (수량 0 이하)")
    void register_Fail_InvalidQuantity() {
        String productId = "testProductId";
        Long quantity = 0L;

        assertThrows(IllegalArgumentException.class, () -> {
            Stock.register(productId, quantity);
        });
    }

    @Test
    @DisplayName("재고 등록 - 실패 (상품 ID null)")
    void register_Fail_NullProductId() {
        Long quantity = 100L;

        assertThrows(NullPointerException.class, () -> {
            Stock.register(null, quantity);
        });
    }

    @Test
    @DisplayName("재고 수량 증가 - 성공")
    void increaseQuantity_Success() {
        Stock stock = Stock.register("testProductId", 100L);
        Long increaseQuantity = 50L;

        stock.increaseQuantity(increaseQuantity);

        assertEquals(150L, stock.getQuantity());
    }

    @Test
    @DisplayName("재고 수량 증가 - 실패 (0 이하)")
    void increaseQuantity_Fail_InvalidQuantity() {
        Stock stock = Stock.register("testProductId", 100L);
        Long increaseQuantity = 0L;

        assertThrows(IllegalArgumentException.class, () -> {
            stock.increaseQuantity(increaseQuantity);
        });
    }

    @Test
    @DisplayName("재고 수량 감소 - 성공")
    void decreaseQuantity_Success() {
        Stock stock = Stock.register("testProductId", 100L);
        Long decreaseQuantity = 50L;

        stock.decreaseQuantity(decreaseQuantity);

        assertEquals(50L, stock.getQuantity());
    }

    @Test
    @DisplayName("재고 수량 감소 - 실패 (0 이하)")
    void decreaseQuantity_Fail_InvalidQuantity() {
        Stock stock = Stock.register("testProductId", 100L);
        Long decreaseQuantity = 0L;

        assertThrows(IllegalArgumentException.class, () -> {
            stock.decreaseQuantity(decreaseQuantity);
        });
    }



    @Test
    @DisplayName("재고 수량 감소 - 실패 (현재 재고보다 많음)")
    void decreaseQuantity_Fail_InsufficientStock() {
        Stock stock = Stock.register("testProductId", 50L);
        Long decreaseQuantity = 100L;

        assertThrows(IllegalArgumentException.class, () -> {
            stock.decreaseQuantity(decreaseQuantity);
        });
    }

    @Test
    @DisplayName("재고 삭제")
    void delete() {
        Stock stock = Stock.register("testProductId", 100L);

        stock.empty();

        assertEquals(0L, stock.checkQuantity());
        assertEquals(stock.getStockStatus(), StockStatus.OUT_OF_STOCK);
    }
}