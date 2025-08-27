package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.cart.exception.InsufficientStockException;
import org.icd4.commerce.application.provided.common.ProductDetailsProvider;
import org.icd4.commerce.application.provided.order.command.CreateOrderCommand;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.order.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
@Transactional
public class CreateOrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final ProductDetailsProvider productDetailsProvider;
    private final InventoryChecker inventoryChecker;

    public Order createOrder(CreateOrderCommand command) {
        OrderId orderId = OrderId.generate();
        //주문 항목 생성
        List<OrderItem> orderItems = IntStream.range(0, command.items().size())
            .mapToObj(i -> {
                var item = command.items().get(i);
                ProductId productId = new ProductId(item.productId());

                ProductDetailsProvider.ProductDetails product = productDetailsProvider.getProductInfo(productId);
                if (!product.active()) {
                    throw new IllegalArgumentException("비활성 상품입니다: " + productId.value());
                }

                int available = inventoryChecker.getAvailableStock(productId);
                if (available < item.quantity()) {
                    throw new InsufficientStockException(productId, available, (int) item.quantity());
                }

                return new OrderItem(
                    OrderItemId.of("1"),
                    orderId,
                    productId,
                    product.name(),
                    product.price().longValue(),
                    item.quantity(),
                    Map.of()
                );
            }).toList();

        Order order = Order.create(
                orderId,
                new CustomerId(command.customerId()),
                orderItems,
                command.orderMessage(),
                command.orderChannel()
        );

        return orderRepository.save(order);
    }
}
