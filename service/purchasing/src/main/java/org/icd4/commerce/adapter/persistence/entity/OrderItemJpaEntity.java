package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="order_items")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderItemJpaEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",
                foreignKey = @ForeignKey(name="fk_order_items_order_id"))
    private OrderJpaEntity order;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Long itemAmount;

    @Lob
    private String productOptions;


    // 생성자
    public OrderItemJpaEntity(
            String id,
            String productId,
            String productName,
            Long unitPrice,
            int quantity,
            Long itemAmount,
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
