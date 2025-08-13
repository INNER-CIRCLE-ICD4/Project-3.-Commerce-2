package org.icd4.commerce.adapter.webapi.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.icd4.commerce.adapter.webapi.common.ErrorResponse;
import org.icd4.commerce.adapter.webapi.dto.order.request.*;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderResponse;
import org.icd4.commerce.application.provided.order.command.ConfirmPurchaseCommand;
import org.icd4.commerce.domain.order.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(name = "Order API", description = "주문 관련 API")
public interface OrderApi {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    OrderResponse createOrder(@RequestBody CreateOrderRequest request);

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> cancelOrder(@PathVariable String id, @RequestBody CancelOrderRequest request);

    @Operation(summary = "결제 완료", description = "결제 완료된 주문 상태로 전환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 완료 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> confirmPayment(@PathVariable String id, @RequestBody ConfirmPaymentRequest request);

    @Operation(summary = "결제 실패", description = "결제 실패로 주문 상태를 전환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 실패 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> failPayment(@PathVariable String id, @RequestBody FailPaymentRequest request);

    @Operation(summary = "구매 확정", description = "주문을 구매 확정 상태로 전환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "구매 확정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> confirmPurchase(@PathVariable UUID id);

    @Operation(summary = "환불 요청", description = "주문 환불을 요청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> requestRefund(@PathVariable String id, @RequestBody RequestRefundRequest request);
}
