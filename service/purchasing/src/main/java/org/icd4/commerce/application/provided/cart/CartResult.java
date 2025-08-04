package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CartItem;
import org.icd4.commerce.domain.cart.CustomerId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 장바구니 조회 결과.
 */
public record CartResult(
    CartId id,
    CustomerId customerId,
    List<CartItemResult> items,
    int totalQuantity,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime lastModifiedAt,
    boolean isConverted
) {
    /**
     * 도메인 모델로부터 결과 객체 생성.
     */
    public static CartResult from(Cart cart, BigDecimal totalAmount) {
        List<CartItemResult> itemResults = cart.getItems().stream()
            .map(CartItemResult::from)
            .toList();
            
        return new CartResult(
            cart.getId(),
            cart.getCustomerId(),
            itemResults,
            cart.getTotalQuantity(),
            totalAmount,
            cart.getCreatedAt(),
            cart.getLastModifiedAt(),
            cart.isConverted()
        );
    }
    
    /**
     * 장바구니 아이템 결과.
     */
    public record CartItemResult(
        String id,
        String productId,
        int quantity,
        ProductOptionsResult options,
        LocalDateTime addedAt
    ) {
        public static CartItemResult from(CartItem item) {
            return new CartItemResult(
                item.getId().value(),
                item.getProductId().value().toString(),
                item.getQuantity(),
                ProductOptionsResult.from(item.getOptions()),
                item.getAddedAt()
            );
        }
    }
    
    /**
     * 상품 옵션 결과.
     */
    public record ProductOptionsResult(
        java.util.Map<String, String> options
    ) {
        public static ProductOptionsResult from(org.icd4.commerce.domain.cart.ProductOptions options) {
            return new ProductOptionsResult(options.getOptions());
        }
    }
}