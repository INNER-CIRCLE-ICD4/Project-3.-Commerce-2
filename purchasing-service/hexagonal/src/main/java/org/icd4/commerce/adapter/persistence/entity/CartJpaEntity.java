package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 JPA 엔티티.
 * 
 * <p>데이터베이스 매핑을 위한 JPA 엔티티로, 순수 도메인 모델과 분리되어 있습니다.
 * 도메인 모델과의 변환은 매퍼를 통해 수행됩니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Entity
@Table(name = "carts")
public class CartJpaEntity {
    
    @Id
    @Column(name = "cart_id")
    private String id;
    
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItemJpaEntity> items = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;
    
    @Column(name = "is_converted", nullable = false)
    private boolean isConverted = false;
    
    /**
     * JPA용 기본 생성자.
     */
    protected CartJpaEntity() {
        // JPA requires no-arg constructor
    }
    
    /**
     * CartJpaEntity 생성자.
     * 
     * @param id 장바구니 ID
     * @param customerId 고객 ID
     */
    public CartJpaEntity(String id, String customerId) {
        this.id = id;
        this.customerId = customerId;
    }
    
    /**
     * 모든 필드를 설정하는 생성자.
     * 
     * @param id 장바구니 ID
     * @param customerId 고객 ID
     * @param createdAt 생성 시간
     * @param lastModifiedAt 마지막 수정 시간
     * @param isConverted 전환 여부
     */
    public CartJpaEntity(String id, String customerId, LocalDateTime createdAt,
                         LocalDateTime lastModifiedAt, boolean isConverted) {
        this.id = id;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.isConverted = isConverted;
    }
    
    /**
     * 장바구니에 아이템을 추가합니다.
     * 
     * @param item 추가할 아이템
     */
    public void addItem(CartItemJpaEntity item) {
        items.add(item);
        item.setCart(this);
    }
    
    /**
     * 장바구니에서 아이템을 제거합니다.
     * 
     * @param item 제거할 아이템
     */
    public void removeItem(CartItemJpaEntity item) {
        items.remove(item);
        item.setCart(null);
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public List<CartItemJpaEntity> getItems() {
        return items;
    }
    
    public void setItems(List<CartItemJpaEntity> items) {
        this.items = items;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }
    
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
    
    public boolean isConverted() {
        return isConverted;
    }
    
    public void setConverted(boolean converted) {
        isConverted = converted;
    }
}