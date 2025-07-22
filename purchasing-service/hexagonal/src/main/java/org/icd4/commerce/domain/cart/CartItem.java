package org.icd4.commerce.domain.cart;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 장바구니 아이템 엔티티 - 순수 도메인 모델.
 * 
 * <p>장바구니에 담긴 개별 상품을 나타내는 순수한 도메인 모델입니다.
 * 각 아이템은 상품 ID, 옵션, 수량 및 가용성 상태를 포함합니다.</p>
 * 
 * <p>주요 비즈니스 규칙:</p>
 * <ul>
 *   <li>수량은 1-99개로 제한</li>
 *   <li>재고 부족 등의 이유로 구매 불가 표시 가능</li>
 *   <li>필수 옵션 검증 기능 제공</li>
 * </ul>
 * 
 * @author Jooeun
 * @since 1.0
 */
public class CartItem {
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 99;
    
    private final CartItemId id;
    private final ProductId productId;
    private final ProductOptions options;
    private final LocalDateTime addedAt;
    private final TimeProvider timeProvider;
    private int quantity;
    private LocalDateTime lastModifiedAt;
    private boolean isAvailable;
    private String unavailableReason;
    
    /**
     * 새로운 CartItem을 생성합니다.
     * 
     * @param id 장바구니 아이템 식별자
     * @param productId 상품 식별자
     * @param options 상품 옵션
     * @param quantity 수량 (1-99)
     * @param timeProvider 시간 제공자
     * @throws NullPointerException 매개변수가 null인 경우
     * @throws InvalidQuantityException 수량이 1-99 범위를 벗어난 경우
     */
    public CartItem(CartItemId id, ProductId productId, ProductOptions options, 
                    int quantity, TimeProvider timeProvider) {
        this.id = Objects.requireNonNull(id, "CartItemId cannot be null");
        this.productId = Objects.requireNonNull(productId, "ProductId cannot be null");
        this.options = Objects.requireNonNull(options, "ProductOptions cannot be null");
        this.timeProvider = Objects.requireNonNull(timeProvider, "TimeProvider cannot be null");
        validateQuantity(quantity);
        this.quantity = quantity;
        this.addedAt = timeProvider.now();
        this.lastModifiedAt = timeProvider.now();
        this.isAvailable = true;
    }
    
    /**
     * 기존 데이터로부터 CartItem을 복원합니다.
     * 
     * @param id 장바구니 아이템 식별자
     * @param productId 상품 식별자
     * @param options 상품 옵션
     * @param quantity 수량
     * @param addedAt 추가된 시간
     * @param lastModifiedAt 마지막 수정 시간
     * @param isAvailable 구매 가능 여부
     * @param unavailableReason 구매 불가 사유
     * @param timeProvider 시간 제공자
     */
    public CartItem(CartItemId id, ProductId productId, ProductOptions options,
                    int quantity, LocalDateTime addedAt, LocalDateTime lastModifiedAt,
                    boolean isAvailable, String unavailableReason, TimeProvider timeProvider) {
        this.id = Objects.requireNonNull(id, "CartItemId cannot be null");
        this.productId = Objects.requireNonNull(productId, "ProductId cannot be null");
        this.options = Objects.requireNonNull(options, "ProductOptions cannot be null");
        this.quantity = quantity;
        this.addedAt = Objects.requireNonNull(addedAt, "AddedAt cannot be null");
        this.lastModifiedAt = Objects.requireNonNull(lastModifiedAt, "LastModifiedAt cannot be null");
        this.isAvailable = isAvailable;
        this.unavailableReason = unavailableReason;
        this.timeProvider = Objects.requireNonNull(timeProvider, "TimeProvider cannot be null");
    }
    
    /**
     * 아이템의 수량을 업데이트합니다.
     * 
     * @param newQuantity 새로운 수량 (1-99)
     * @throws InvalidQuantityException 수량이 1-99 범위를 벗어난 경우
     */
    public void updateQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        this.lastModifiedAt = timeProvider.now();
    }
    
    /**
     * 아이템의 수량을 증가시킵니다.
     * 
     * @param additionalQuantity 추가할 수량
     * @throws InvalidQuantityException 결과 수량이 99개를 초과하는 경우
     */
    public void increaseQuantity(int additionalQuantity) {
        int newQuantity = this.quantity + additionalQuantity;
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        this.lastModifiedAt = timeProvider.now();
    }
    
    /**
     * 아이템을 구매 불가 상태로 표시합니다.
     * 
     * @param reason 구매 불가 사유 (예: "재고 부족", "판매 종료")
     */
    public void markAsUnavailable(String reason) {
        this.isAvailable = false;
        this.unavailableReason = reason;
        this.lastModifiedAt = timeProvider.now();
    }
    
    /**
     * 아이템을 구매 가능 상태로 표시합니다.
     */
    public void markAsAvailable() {
        this.isAvailable = true;
        this.unavailableReason = null;
        this.lastModifiedAt = timeProvider.now();
    }
    
    /**
     * 동일한 상품인지 확인합니다.
     * 상품 ID와 옵션이 모두 동일해야 같은 상품으로 판단합니다.
     * 
     * @param otherProductId 비교할 상품 ID
     * @param otherOptions 비교할 상품 옵션
     * @return 동일한 상품이면 true
     */
    public boolean isSameProduct(ProductId otherProductId, ProductOptions otherOptions) {
        return this.productId.equals(otherProductId) && this.options.equals(otherOptions);
    }
    
    private void validateQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new InvalidQuantityException(
                String.format("Quantity must be between %d and %d, but was %d", 
                    MIN_QUANTITY, MAX_QUANTITY, quantity)
            );
        }
    }
    
    /**
     * 필수 옵션이 모두 선택되었는지 검증합니다.
     * 
     * @param requiredOptions 옵션명과 필수 여부를 나타내는 Map
     * @throws RequiredOptionMissingException 필수 옵션이 누락된 경우
     */
    public void validateRequiredOptions(Map<String, Boolean> requiredOptions) {
        for (Map.Entry<String, Boolean> entry : requiredOptions.entrySet()) {
            String optionKey = entry.getKey();
            boolean isRequired = entry.getValue();
            
            if (isRequired && !options.hasOption(optionKey)) {
                throw new RequiredOptionMissingException(
                    String.format("Required option '%s' is missing", optionKey)
                );
            }
        }
    }
    
    // Getters
    public CartItemId getId() {
        return id;
    }
    
    public ProductId getProductId() {
        return productId;
    }
    
    public ProductOptions getOptions() {
        return options;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public LocalDateTime getAddedAt() {
        return addedAt;
    }
    
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public String getUnavailableReason() {
        return unavailableReason;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}