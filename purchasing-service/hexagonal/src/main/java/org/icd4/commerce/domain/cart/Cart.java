package org.icd4.commerce.domain.cart;

import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.CartItemLimitExceededException;
import org.icd4.commerce.domain.cart.exception.InvalidCartStateException;
import org.icd4.commerce.domain.cart.exception.InvalidQuantityException;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.ProductPriceProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 장바구니 애그리게이트 루트 - 순수 도메인 모델.
 * 
 * <p>JPA나 다른 인프라스트럭처 의존성이 없는 순수한 도메인 모델입니다.
 * 모든 비즈니스 로직과 불변성 규칙을 완벽하게 캡슐화합니다.</p>
 * 
 * <p>주요 비즈니스 규칙:</p>
 * <ul>
 *   <li>최대 50개의 서로 다른 상품 타입 보관 가능</li>
 *   <li>동일 상품과 옵션 추가 시 수량 증가</li>
 *   <li>주문으로 전환된 후에는 수정 불가</li>
 *   <li>빈 장바구니는 주문으로 전환 불가</li>
 *   <li>90일간 수정이 없으면 만료</li>
 * </ul>
 * 
 * @author Jooeun
 * @since 1.0
 */
public class Cart {
    private static final int MAX_ITEM_TYPES = 50;
    private static final int CART_EXPIRY_DAYS = 90;
    
    private final CartId id;
    private final CustomerId customerId;
    private final List<CartItem> items;
    private final LocalDateTime createdAt;
    private final TimeProvider timeProvider;
    private LocalDateTime lastModifiedAt;
    private boolean isConverted;
    
    /**
     * 새로운 장바구니를 생성합니다.
     * 
     * @param id 장바구니 식별자
     * @param customerId 고객 식별자
     * @param timeProvider 시간 제공자
     * @throws NullPointerException 매개변수가 null인 경우
     */
    public Cart(CartId id, CustomerId customerId, TimeProvider timeProvider) {
        this.id = requireNonNull(id, "CartId cannot be null");
        this.customerId = requireNonNull(customerId, "CustomerId cannot be null");
        this.timeProvider = requireNonNull(timeProvider, "TimeProvider cannot be null");
        this.items = new ArrayList<>();
        this.createdAt = timeProvider.now();
        this.lastModifiedAt = timeProvider.now();
        this.isConverted = false;
    }
    
    /**
     * 기존 데이터로부터 장바구니를 복원합니다.
     * 
     * @param id 장바구니 식별자
     * @param customerId 고객 식별자
     * @param items 장바구니 아이템 목록
     * @param createdAt 생성 시간
     * @param lastModifiedAt 마지막 수정 시간
     * @param isConverted 전환 여부
     * @param timeProvider 시간 제공자
     */
    public Cart(CartId id, CustomerId customerId, List<CartItem> items,
                LocalDateTime createdAt, LocalDateTime lastModifiedAt, 
                boolean isConverted, TimeProvider timeProvider) {
        this.id = requireNonNull(id, "CartId cannot be null");
        this.customerId = requireNonNull(customerId, "CustomerId cannot be null");
        this.items = new ArrayList<>(requireNonNull(items, "Items cannot be null"));
        this.createdAt = requireNonNull(createdAt, "CreatedAt cannot be null");
        this.lastModifiedAt = requireNonNull(lastModifiedAt, "LastModifiedAt cannot be null");
        this.isConverted = isConverted;
        this.timeProvider = requireNonNull(timeProvider, "TimeProvider cannot be null");
    }
    
    /**
     * 장바구니에 상품을 추가합니다.
     * 
     * @param productId 상품 식별자
     * @param quantity 추가할 수량 (1-99)
     * @param options 상품 옵션
     * @throws CartAlreadyConvertedException 이미 주문으로 전환된 경우
     * @throws CartItemLimitExceededException 상품 종류가 50개를 초과하는 경우
     * @throws InvalidQuantityException 수량이 범위를 벗어난 경우
     */
    public void addItem(ProductId productId, int quantity, ProductOptions options) {
        ensureNotConverted();
        
        Optional<CartItem> existingItem = findItemByProductAndOptions(productId, options);
        
        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            if (items.size() >= MAX_ITEM_TYPES) {
                throw new CartItemLimitExceededException(
                    String.format("Cannot add more than %d different product types", MAX_ITEM_TYPES)
                );
            }
            
            CartItemId itemId = CartItemId.generate();
            CartItem newItem = new CartItem(itemId, productId, options, quantity, timeProvider);
            items.add(newItem);
        }
        
        updateLastModifiedAt();
    }
    
    /**
     * 장바구니에서 특정 아이템을 제거합니다.
     * 
     * @param cartItemId 제거할 아이템의 식별자
     * @throws CartAlreadyConvertedException 이미 주문으로 전환된 경우
     * @throws InvalidCartStateException 해당 아이템이 존재하지 않는 경우
     */
    public void removeItem(CartItemId cartItemId) {
        ensureNotConverted();
        
        boolean removed = items.removeIf(item -> item.getId().equals(cartItemId));
        if (!removed) {
            throw new InvalidCartStateException("Cart item not found: " + cartItemId);
        }
        
        updateLastModifiedAt();
    }
    
    /**
     * 장바구니 아이템의 수량을 변경합니다.
     * 
     * @param cartItemId 수량을 변경할 아이템의 식별자
     * @param quantity 새로운 수량 (1-99)
     * @throws CartAlreadyConvertedException 이미 주문으로 전환된 경우
     * @throws InvalidCartStateException 해당 아이템이 존재하지 않는 경우
     * @throws InvalidQuantityException 수량이 범위를 벗어난 경우
     */
    public void updateQuantity(CartItemId cartItemId, int quantity) {
        ensureNotConverted();
        
        CartItem item = findItemById(cartItemId)
            .orElseThrow(() -> new InvalidCartStateException("Cart item not found: " + cartItemId));
        
        item.updateQuantity(quantity);
        updateLastModifiedAt();
    }
    
    /**
     * 장바구니의 모든 아이템을 제거합니다.
     * 
     * @throws CartAlreadyConvertedException 이미 주문으로 전환된 경우
     */
    public void clear() {
        ensureNotConverted();
        items.clear();
        updateLastModifiedAt();
    }
    
    /**
     * 장바구니의 총 금액을 계산합니다.
     * 
     * @param priceProvider 상품 가격 제공자
     * @return 총 금액
     * @throws NullPointerException priceProvider가 null인 경우
     */
    public BigDecimal calculateTotal(ProductPriceProvider priceProvider) {
        requireNonNull(priceProvider, "PriceProvider cannot be null");
        
        return items.stream()
            .map(item -> {
                BigDecimal price = priceProvider.getPrice(item.getProductId());
                return price.multiply(BigDecimal.valueOf(item.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 다른 장바구니의 아이템들을 현재 장바구니에 병합합니다.
     * 
     * @param otherCart 병합할 장바구니
     * @throws CartAlreadyConvertedException 현재 장바구니가 이미 전환된 경우
     * @throws InvalidCartStateException 병합 대상 장바구니가 이미 전환된 경우
     * @throws CartItemLimitExceededException 병합 시 상품 종류가 50개를 초과하는 경우
     */
    public void merge(Cart otherCart) {
        ensureNotConverted();
        
        if (otherCart.isConverted()) {
            throw new InvalidCartStateException("Cannot merge a converted cart");
        }
        
        for (CartItem otherItem : otherCart.getItems()) {
            addItem(otherItem.getProductId(), otherItem.getQuantity(), otherItem.getOptions());
        }
    }
    
    /**
     * 장바구니를 주문으로 전환합니다.
     * 
     * @throws CartAlreadyConvertedException 이미 전환된 경우
     * @throws InvalidCartStateException 장바구니가 비어있는 경우
     */
    public void convertToOrder() {
        ensureNotConverted();
        
        if (items.isEmpty()) {
            throw new InvalidCartStateException("Cannot convert an empty cart");
        }
        
        isConverted = true;
        updateLastModifiedAt();
    }
    
    /**
     * 실패한 주문으로부터 장바구니를 복원합니다.
     * 
     * @param cartId 복원할 장바구니 ID
     * @param customerId 고객 ID
     * @param items 복원할 아이템 목록
     * @param timeProvider 시간 제공자
     * @return 복원된 장바구니
     * @throws NullPointerException 매개변수가 null인 경우
     */
    public static Cart restoreFromFailedOrder(
            CartId cartId, 
            CustomerId customerId, 
            List<CartItem> items,
            TimeProvider timeProvider) {
        requireNonNull(cartId, "CartId cannot be null");
        requireNonNull(customerId, "CustomerId cannot be null");
        requireNonNull(items, "Items cannot be null");
        requireNonNull(timeProvider, "TimeProvider cannot be null");
        
        LocalDateTime now = timeProvider.now();
        return new Cart(
            cartId,
            customerId,
            new ArrayList<>(items),
            now,
            now,
            false,
            timeProvider
        );
    }
    
    /**
     * 장바구니가 만료되었는지 확인합니다.
     * 
     * @return 만료 여부
     */
    public boolean isExpired() {
        return lastModifiedAt.plusDays(CART_EXPIRY_DAYS).isBefore(timeProvider.now());
    }
    
    private void ensureNotConverted() {
        if (isConverted) {
            throw new CartAlreadyConvertedException("Cannot modify a converted cart");
        }
    }
    
    private Optional<CartItem> findItemByProductAndOptions(ProductId productId, ProductOptions options) {
        return items.stream()
            .filter(item -> item.isSameProduct(productId, options))
            .findFirst();
    }
    
    private Optional<CartItem> findItemById(CartItemId cartItemId) {
        return items.stream()
            .filter(item -> item.getId().equals(cartItemId))
            .findFirst();
    }
    
    private void updateLastModifiedAt() {
        this.lastModifiedAt = timeProvider.now();
    }
    
    private static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }
    
    // Getters - 모두 불변 객체 또는 방어적 복사본 반환
    public CartId getId() {
        return id;
    }
    
    public CustomerId getCustomerId() {
        return customerId;
    }
    
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }
    
    public boolean isConverted() {
        return isConverted;
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public int getTotalQuantity() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
}