package org.icd4.commerce.adapter.webapi;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.order.request.*;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderResponse;
import org.icd4.commerce.adapter.webapi.spec.OrderApi;
import org.icd4.commerce.application.provided.order.command.ConfirmPurchaseCommand;
import org.icd4.commerce.application.provided.order.usecase.*;
import org.icd4.commerce.domain.order.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        Order order = createOrderUseCase.execute(request.toCommand());
        return OrderResponse.from(order);
    }

    // 주문 취소
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id, @RequestBody CancelOrderRequest request) {
        cancelOrderUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

    // 결제 성공
    @PostMapping("/{id}/confirmPayment")
    public ResponseEntity<Void> confirmPayment(@PathVariable String id, @RequestBody ConfirmPaymentRequest request) {
        confirmPaymentUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

    // 결제 실패
    @PostMapping("/{id}/failPayment")
    public ResponseEntity<Void> failPayment(@PathVariable String id, @RequestBody FailPaymentRequest request) {
        failPaymentUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

    // 구매 확정
    @PostMapping("/{id}/confirmPurchase")
    public ResponseEntity<Void> confirmPurchase(@PathVariable UUID id) {
        confirmPurchaseUseCase.execute(new ConfirmPurchaseCommand(id));
        return ResponseEntity.ok().build();
    }

    // 환불 요청
    @PostMapping("/{id}/refund")
    public ResponseEntity<Void> requestRefund(@PathVariable String id, @RequestBody RequestRefundRequest request) {
        requestRefundUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

}
