package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="order_items")
@Getter
@NoArgsConstructor
public class OrderItemJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private long unitPrice;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private long itemAmount;

    @Lob
    private String productOptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    // 생성자
    public OrderItemJpaEntity(
            UUID id,
            String productId,
            String productName,
            long unitPrice,
            long quantity,
            long itemAmount,
            String productOptions
    ) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.itemAmount = itemAmount;
        this.productOptions = productOptions;
    }

    public void setOrder(OrderJpaEntity order) {
        if (this.order != null) {
            this.order.getOrderItems().remove(this);
        }
        this.order = order;
        if (order != null && !order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }
}
