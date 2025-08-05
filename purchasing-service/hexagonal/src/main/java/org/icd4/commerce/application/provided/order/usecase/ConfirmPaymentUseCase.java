package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.ConfirmPaymentCommand;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.application.required.common.InventoryReducer;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.icd4.commerce.domain.order.PaymentId;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ConfirmPaymentUseCase {

    private final OrderRepositoryPort orderRepository;
    private final InventoryChecker inventoryChecker;
    private final InventoryReducer inventoryReducer;

    public void execute(ConfirmPaymentCommand command) {
        OrderId orderId = new OrderId(command.orderId());
        PaymentId paymentId = new PaymentId(command.paymentId());

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("해당 주문이 존재하지 않습니다: " + command.orderId());
        }

        Order order = orderOptional.get();

        //1.결제 성공
        order.confirmPayment(paymentId);

        //2.재고 확인
        boolean outOfStock = order.getOrderItems().stream().anyMatch(item -> {
            int available = inventoryChecker.getAvailableStock(item.getProductId());
            return available < item.getQuantity();
        });

        if (outOfStock) {
            //3-1.재고 부족 → 결제 취소 + 주문 취소 처리
            order.failPayment();
            orderRepository.save(order);
            throw new IllegalStateException("결제는 성공했지만 재고가 부족하여 주문이 취소되었습니다.");
        }

        //3-2.재고 충분 → 재고 차감
        order.getOrderItems().forEach(item -> {
            inventoryReducer.reduce(item.getProductId(), item.getQuantity());
        });

        orderRepository.save(order);
    }
}
