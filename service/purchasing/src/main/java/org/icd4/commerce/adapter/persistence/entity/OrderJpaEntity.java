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
