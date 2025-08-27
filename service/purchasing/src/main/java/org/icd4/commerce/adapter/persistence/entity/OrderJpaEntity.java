package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private String orderMessage;

    private String paymentId;

    private String orderChannel;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemJpaEntity> orderItems;

    /**
     * 모든 필드를 설정하는 생성자
     */
    private OrderJpaEntity(
            String id,
            String customerId,
            OrderStatus orderStatus,
            BigDecimal totalAmount,
            String orderMessage,
            String paymentId,
            String orderChannel,
            LocalDateTime createdAt,
            LocalDateTime lastModifiedAt,
            LocalDateTime completedAt,
            List<OrderItemJpaEntity> orderItems
    ) {
        this.id = id;
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.orderMessage = orderMessage;
        this.paymentId = paymentId;
        this.orderChannel = orderChannel;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.completedAt = completedAt;
        if (orderItems != null) setOrderItems(orderItems);
    }

    //static factory
    public static OrderJpaEntity of(
            String id,
            String customerId,
            OrderStatus orderStatus,
            BigDecimal totalAmount,
            String orderMessage,
            String paymentId,
            String orderChannel,
            LocalDateTime createdAt,
            LocalDateTime lastModifiedAt,
            LocalDateTime completedAt
    ) {
        return new OrderJpaEntity(
                id,
                customerId,
                orderStatus,
                totalAmount,
                orderMessage,
                paymentId,
                orderChannel,
                createdAt,
                lastModifiedAt,
                completedAt,
                null
        );
    }

    public void setOrderItems(List<OrderItemJpaEntity> itemEntities) {
        if (this.orderItems == null) this.orderItems = new ArrayList<>();
        //역방향 끊기
        for (var it : this.orderItems) it.setOrder(null);
        this.orderItems.clear();

        if (itemEntities == null) return;

        for (var it : itemEntities) {
            this.orderItems.add(it);
            it.setOrder(this);
        }
    }
}
