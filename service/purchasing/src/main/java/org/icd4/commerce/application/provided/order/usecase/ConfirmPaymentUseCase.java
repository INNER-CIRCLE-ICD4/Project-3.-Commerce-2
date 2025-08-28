package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderStatusResponse;
import org.icd4.commerce.application.provided.order.command.ConfirmPaymentCommand;
import org.icd4.commerce.application.provided.order.support.OrderLoader;
import org.icd4.commerce.application.required.common.InventoryReducer;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ConfirmPaymentUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderLoader orderLoader;
    private final InventoryReducer inventoryReducer;

    public OrderStatusResponse confirmPayment(ConfirmPaymentCommand command) {
        Order order = orderLoader.findById(command.orderId());

        //재고 차감 요청
        order.getOrderItems().forEach(item -> {
            inventoryReducer.reduceStock(item.getSku(), item.getQuantity());
        });

        order.confirmPayment(command.paymentId());
        return OrderStatusResponse.from(orderRepository.save(order));
    }
}
