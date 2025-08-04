package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 장바구니 아이템 JPA 엔티티.
 * 
 * <p>데이터베이스 매핑을 위한 JPA 엔티티로, 순수 도메인 모델과 분리되어 있습니다.
 * 도메인 모델과의 변환은 매퍼를 통해 수행됩니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Entity
@Table(name = "cart_items")
public class CartItemJpaEntity {
    
    @Id
    @Column(name = "cart_item_id")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartJpaEntity cart;
    
    @Column(name = "product_id", nullable = false)
    private String productId;

    /**
     * 1. 데이터베이스 호환성 - JSON은 대부분 지원 (Map X)
     * 2. 헥사고날 아키텍처 원칙 - 인프라 레이어는 저장방식에 집중, 도메인 레이어는 비즈니스 로직에 집중
     * 3. 유연성과 확장성 - 나중에 옵션이 복잡해져도 커버 가능
     */
    @Column(name = "product_options", columnDefinition = "JSON")
    private String options;

    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;
    
    @UpdateTimestamp
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;
    
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;
    
    @Column(name = "unavailable_reason")
    private String unavailableReason;
    
    /**
     * JPA용 기본 생성자.
     */
    protected CartItemJpaEntity() {
        // JPA requires no-arg constructor
    }
    
    /**
     * 테스트 및 객체 생성을 위한 생성자.
     * Cart는 별도로 설정해야 합니다.
     * 
     * @param id 장바구니 아이템 ID
     * @param productId 상품 ID
     * @param options 상품 옵션 (JSON 문자열)
     * @param quantity 수량
     */
    public CartItemJpaEntity(String id, String productId, String options, int quantity) {
        this.id = id;
        this.productId = productId;
        this.options = options;
        this.quantity = quantity;
        this.isAvailable = true;
    }
    
    /**
     * 모든 필드를 설정하는 생성자.
     * 
     * @param id 장바구니 아이템 ID
     * @param productId 상품 ID
     * @param options 상품 옵션 (JSON 문자열)
     * @param quantity 수량
     * @param addedAt 추가된 시간
     * @param lastModifiedAt 마지막 수정 시간
     * @param isAvailable 구매 가능 여부
     * @param unavailableReason 구매 불가 사유
     */
    public CartItemJpaEntity(String id, String productId, String options, int quantity,
                            LocalDateTime addedAt, LocalDateTime lastModifiedAt,
                            boolean isAvailable, String unavailableReason) {
        this.id = id;
        this.productId = productId;
        this.options = options;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.lastModifiedAt = lastModifiedAt;
        this.isAvailable = isAvailable;
        this.unavailableReason = unavailableReason;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }

    public CartJpaEntity getCart() {
        return cart;
    }

    public void setCart(CartJpaEntity cart) {
        this.cart = cart;
    }

    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getOptions() {
        return options;
    }
    
    public void setOptions(String options) {
        this.options = options;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
    
}