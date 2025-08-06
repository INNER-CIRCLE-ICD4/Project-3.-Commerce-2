package org.icd4.commerce.adapter.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.icd4.commerce.adapter.persistence.entity.OrderItemJpaEntity;
import org.icd4.commerce.adapter.persistence.entity.OrderJpaEntity;
import org.icd4.commerce.domain.cart.ProductOptions;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.order.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

    private final ObjectMapper objectMapper;

    public OrderEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    //도메인 → JPA: Order
    public OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getOrderId().value().toString(),
                order.getCustomerId().value(),
                order.getOrderStatus(),
                order.getTotalAmount().getAmount(),
                order.getOrderMessage(),
                order.getPaymentId() != null ? String.valueOf(order.getPaymentId().value()) : null,
                order.getOrderChannel(),
                order.getCreatedAt(),
                order.getLastModifiedAt(),
                order.getCompletedAt(),
                order.getOrderItems().stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList())
        );

        // 연관관계 설정
        List<OrderItemJpaEntity> itemEntities = order.getOrderItems().stream()
                .map(item -> toItemEntity(item, entity))
                .collect(Collectors.toList());
        entity.setOrderItems(itemEntities);

        return entity;
    }

    private OrderItemJpaEntity toEntity(OrderItem item) {
        return new OrderItemJpaEntity(
                item.getOrderItemId().value(),
                item.getProductId().value(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getItemAmount(),
                serializeOptions(ProductOptions.of(item.getProductOptions()))
        );
    }

    //JPA → 도메인: Order
    public Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getOrderItems().stream()
                .map(this::toItemDomain)
                .collect(Collectors.toList());

        Order order = Order.restore(
                new OrderId(UUID.fromString(entity.getId())),
                new CustomerId(entity.getCustomerId()),
                items,
                entity.getOrderStatus(),
                Money.of(entity.getTotalAmount()),
                entity.getOrderMessage(),
                entity.getPaymentId() != null ? new PaymentId(UUID.fromString(entity.getPaymentId())) : null,
                entity.getOrderChannel(),
                entity.getCreatedAt(),
                entity.getLastModifiedAt(),
                entity.getCompletedAt()
        );

        return order;
    }

    //도메인 → JPA: OrderItem
    private OrderItemJpaEntity toItemEntity(OrderItem item, OrderJpaEntity order) {
        String optionsJson = serializeOptions(ProductOptions.of(item.getProductOptions()));

        OrderItemJpaEntity entity = new OrderItemJpaEntity(
                item.getOrderItemId().value(),
                item.getProductId().value(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getItemAmount(),
                optionsJson
        );
        entity.setOrder(order); // 연관관계 설정
        return entity;
    }

    //JPA → 도메인: OrderItem
    private OrderItem toItemDomain(OrderItemJpaEntity entity) {
        ProductOptions options = deserializeOptions(entity.getProductOptions());

        return new OrderItem(
                new OrderItemId(entity.getId()),
                new OrderId(entity.getId()),
                new ProductId(entity.getProductId().toString()),
                entity.getProductName(),
                entity.getUnitPrice(),
                entity.getQuantity(),
                options.getOptions()
        );
    }

    //옵션 직렬화
    private String serializeOptions(ProductOptions options) {
        try {
            return objectMapper.writeValueAsString(options.getOptions());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ProductOptions 직렬화 실패", e);
        }
    }

    //옵션 역직렬화
    private ProductOptions deserializeOptions(String json) {
        if (json == null || json.isBlank()) {
            return new ProductOptions(Map.of());
        }
        try {
            Map<String, String> options = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
            return new ProductOptions(options);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ProductOptions 역직렬화 실패", e);
        }
    }
}
