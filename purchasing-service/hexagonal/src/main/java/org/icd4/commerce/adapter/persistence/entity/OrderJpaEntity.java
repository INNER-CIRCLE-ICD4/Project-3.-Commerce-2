package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.icd4.commerce.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class OrderJpaEntity {

    @Id
    @Column(name = "order_id")
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "order_message")
    private String orderMessage;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "order_channel")
    private String orderChannel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemJpaEntity> orderItems;

    /**
     * 모든 필드를 설정하는 생성자
     */
    public OrderJpaEntity(
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

    }

    public void setOrderItems(List<OrderItemJpaEntity> itemEntities) {
    }
}
