package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.cart.exception.InsufficientStockException;
import org.icd4.commerce.application.provided.common.ProductDetailsProvider;
import org.icd4.commerce.application.provided.common.ProductDetailsProvider.ProductDetails;
import org.icd4.commerce.application.provided.order.command.CreateOrderCommand;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.application.required.common.InventoryChecker.AvailableStock;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;
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
                    ProductId productId = ProductId.of(item.productId());
                    StockKeepingUnit sku = StockKeepingUnit.of(item.sku());

                    ProductDetails product = productDetailsProvider.getProductInfo(productId, sku);

                    AvailableStock availableStock = inventoryChecker.getAvailableStock(sku);
                    if (!availableStock.isAvailable(item.quantity())) {
                        throw new InsufficientStockException(productId, availableStock.availableStock(), item.quantity());
                    }

                    return new OrderItem(
                            OrderItemId.generate(),
                            orderId,
                            productId,
                            sku,
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

    public Order createOrder2(CreateOrderCommand command) {
        OrderId orderId = OrderId.generate();
        //주문 항목 생성
        List<OrderItem> orderItems = command.items().stream()
                .map(item -> {
                    ProductId productId = ProductId.of(item.productId());
                    StockKeepingUnit sku = StockKeepingUnit.of(item.sku());

                    ProductDetails product = productDetailsProvider.getProductInfo(productId, sku);

                    AvailableStock availableStock = inventoryChecker.getAvailableStock(sku);
                    if (!availableStock.isAvailable(item.quantity())) {
                        throw new InsufficientStockException(productId, availableStock.availableStock(), item.quantity());
                    }

                    return new OrderItem(
                            OrderItemId.generate(),
                            orderId,
                            productId,
                            sku,
                            product.name(),
                            product.price().longValue(),
                            item.quantity(),
                            Map.of());
                }).toList();

        Order order = Order.create(
                orderId,
                CustomerId.of(command.customerId()),
                orderItems,
                command.orderMessage(),
                command.orderChannel()
        );

        return orderRepository.save(order);
    }
}
