package org.icd4.commerce.adapter.webapi;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.order.request.*;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderResponse;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderStatusResponse;
import org.icd4.commerce.adapter.webapi.spec.OrderApi;
import org.icd4.commerce.application.provided.order.command.ConfirmPurchaseCommand;
import org.icd4.commerce.application.provided.order.usecase.*;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ConfirmPaymentUseCase confirmPaymentUseCase;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
    private final FailPaymentUseCase failPaymentUseCase;
    private final RequestRefundUseCase requestRefundUseCase;

    // 주문 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        Order order = createOrderUseCase.createOrder2(request.toCommand());
        return OrderResponse.from(order);
    }

    // 주문 취소
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderStatusResponse> cancelOrder(@PathVariable String orderId,
                                                            @RequestBody CancelOrderRequest request) {
        return ResponseEntity.ok().body(cancelOrderUseCase.cancelOrder(request.toCommand(orderId)));
    }

    // 결제 성공
    @PatchMapping("/{orderId}/confirmPayment")
    public ResponseEntity<OrderStatusResponse> confirmPayment(@PathVariable String orderId, @RequestBody ConfirmPaymentRequest request) {
        return ResponseEntity.ok().body(confirmPaymentUseCase.confirmPayment(request.toCommand(orderId)));
    }

    // 결제 실패
    @PatchMapping("/{orderId}/failPayment")
    public ResponseEntity<OrderStatusResponse> failPayment(@PathVariable String orderId, @RequestBody FailPaymentRequest request) {
        return ResponseEntity.ok().body(failPaymentUseCase.failPayment(request.toCommand(orderId)));
    }

    // 구매 확정
    @PatchMapping("/{orderId}/confirmPurchase")
    public ResponseEntity<Void> confirmPurchase(@PathVariable String orderId) {
        confirmPurchaseUseCase.confirmPurchase(new ConfirmPurchaseCommand(OrderId.from(orderId)));
        return ResponseEntity.ok().build();
    }

    // 환불 요청
    @PatchMapping("/{orderId}/refund")
    public ResponseEntity<Void> requestRefund(@PathVariable String orderId, @RequestBody RequestRefundRequest request) {
        requestRefundUseCase.requestRefund(request.toCommand(orderId));
        return ResponseEntity.ok().build();
    }

}
