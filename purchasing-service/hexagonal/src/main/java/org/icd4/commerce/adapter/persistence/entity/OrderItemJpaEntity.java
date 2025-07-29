package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="order_items")
@Getter
@NoArgsConstructor
public class OrderItemJpaEntity {

    @Id
    private String id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private long unitPrice;

    @Column(nullable = false)
    private long quantity;

    @Column(name = "item_amount",nullable = false)
    private long itemAmount;

    @Lob
    @Column(name = "product_options")
    private String productOptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    // 생성자
    public OrderItemJpaEntity(
            String id,
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
