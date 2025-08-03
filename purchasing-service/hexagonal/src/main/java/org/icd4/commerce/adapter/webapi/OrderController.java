package org.icd4.commerce.adapter.webapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.common.ErrorResponse;
import org.icd4.commerce.adapter.webapi.dto.order.request.*;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderResponse;
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
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ConfirmPaymentUseCase confirmPaymentUseCase;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
    private final FailPaymentUseCase failPaymentUseCase;
    private final RequestRefundUseCase requestRefundUseCase;

    // 주문 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        Order order = createOrderUseCase.execute(request.toCommand());
        return OrderResponse.from(order);
    }

    // 주문 취소
    @PostMapping("/{id}/cancel")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id, @RequestBody CancelOrderRequest request) {
        cancelOrderUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

    // 결제 성공
    @PostMapping("/{id}/confirmPayment")
    @Operation(summary = "결제 완료 처리", description = "결제 완료된 주문 상태로 전환합니다.")
    public ResponseEntity<Void> confirmPayment(@PathVariable String id, @RequestBody ConfirmPaymentRequest request) {
        confirmPaymentUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

    // 결제 실패
    @PostMapping("/{id}/failPayment")
    @Operation(summary = "결제 실패 처리", description = "결제 실패로 주문 상태를 전환합니다.")
    public ResponseEntity<Void> failPayment(@PathVariable String id, @RequestBody FailPaymentRequest request) {
        failPaymentUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

    // 구매 확정
    @PostMapping("/{id}/confirmPurchase")
    @Operation(summary = "구매 확정", description = "주문을 구매 확정 상태로 전환합니다.")
    public ResponseEntity<Void> confirmPurchase(@PathVariable UUID id) {
        confirmPurchaseUseCase.execute(new ConfirmPurchaseCommand(id));
        return ResponseEntity.ok().build();
    }

    // 환불 요청
    @PostMapping("/{id}/refund")
    @Operation(summary = "환불 요청", description = "주문 환불을 요청합니다.")
    public ResponseEntity<Void> requestRefund(@PathVariable String id, @RequestBody RequestRefundRequest request) {
        requestRefundUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok().build();
    }

}
