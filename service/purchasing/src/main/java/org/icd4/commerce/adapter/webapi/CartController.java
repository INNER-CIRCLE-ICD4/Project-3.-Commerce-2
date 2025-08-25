package org.icd4.commerce.adapter.webapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.webapi.common.ErrorResponse;
import org.icd4.commerce.adapter.webapi.dto.*;
import org.icd4.commerce.adapter.webapi.dto.cart.request.AddItemRequest;
import org.icd4.commerce.adapter.webapi.dto.cart.request.CreateCartRequest;
import org.icd4.commerce.adapter.webapi.dto.cart.request.MergeCartsRequest;
import org.icd4.commerce.adapter.webapi.dto.cart.request.UpdateQuantityRequest;
import org.icd4.commerce.adapter.webapi.dto.cart.response.CartResponse;
import org.icd4.commerce.adapter.webapi.dto.cart.response.CreateCartResponse;
import org.icd4.commerce.application.provided.cart.*;
import org.icd4.commerce.application.provided.cart.command.ClearCartCommand;
import org.icd4.commerce.application.provided.cart.command.RemoveItemFromCartCommand;
import org.icd4.commerce.application.provided.cart.usecase.*;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CartItemId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 장바구니 REST API 컨트롤러.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "장바구니 관리 API")
public class CartController {
    
    private final CreateCartUseCase createCartUseCase;
    private final GetCartUseCase getCartUseCase;
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final MergeCartsUseCase mergeCartsUseCase;
    
    /**
     * 장바구니 생성.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "장바구니 생성", description = "새로운 장바구니를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "장바구니 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public org.icd4.commerce.adapter.webapi.common.ApiResponse<CreateCartResponse> createCart(
            @Valid @RequestBody CreateCartRequest request) {
        
        log.info("Creating cart for customer: {}", request.customerId());
        
        CartId cartId = createCartUseCase.execute(request.toCommand());
        
        return org.icd4.commerce.adapter.webapi.common.ApiResponse.success(
            CreateCartResponse.from(cartId)
        );
    }
    
    /**
     * 장바구니 조회.
     */
    @GetMapping("/{cartId}")
    @Operation(summary = "장바구니 조회", description = "장바구니 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public org.icd4.commerce.adapter.webapi.common.ApiResponse<CartResponse> getCart(
            @Parameter(description = "장바구니 ID", example = "cart-abc123")
            @PathVariable String cartId) {
        
        log.info("Getting cart: {}", cartId);
        
        CartResult result = getCartUseCase.execute(CartId.of(cartId));
        
        return org.icd4.commerce.adapter.webapi.common.ApiResponse.success(
            CartResponse.from(result)
        );
    }
    
    /**
     * 장바구니에 상품 추가.
     */
    @PostMapping("/{cartId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "상품 추가", description = "장바구니에 상품을 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "상품 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public org.icd4.commerce.adapter.webapi.common.ApiResponse<Void> addItem(
            @Parameter(description = "장바구니 ID", example = "cart-abc123")
            @PathVariable String cartId,
            @Valid @RequestBody AddItemRequest request) {
        
        log.info("Adding item to cart. CartId: {}, ProductId: {}, Quantity: {}",
            cartId, request.productId(), request.quantity());
        
        addItemToCartUseCase.execute(request.toCommand(cartId));
        
        return org.icd4.commerce.adapter.webapi.common.ApiResponse.success();
    }
    
    /**
     * 장바구니에서 상품 제거.
     */
    @DeleteMapping("/{cartId}/items/{itemId}")
    @Operation(summary = "상품 제거", description = "장바구니에서 특정 상품을 제거합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상품 제거 성공"),
        @ApiResponse(responseCode = "404", description = "장바구니 또는 상품을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public org.icd4.commerce.adapter.webapi.common.ApiResponse<Void> removeItem(
            @Parameter(description = "장바구니 ID", example = "cart-abc123")
            @PathVariable String cartId,
            @Parameter(description = "아이템 ID", example = "item-xyz789")
            @PathVariable String itemId) {
        
        log.info("Removing item from cart. CartId: {}, ItemId: {}", cartId, itemId);
        
        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(
            CartId.of(cartId),
            CartItemId.of(itemId)
        );
        
        removeItemFromCartUseCase.execute(command);
        
        return org.icd4.commerce.adapter.webapi.common.ApiResponse.success();
    }
    
    /**
     * 장바구니 상품 수량 변경.
     */
    @PatchMapping("/{cartId}/items/{itemId}")
    @Operation(summary = "수량 변경", description = "장바구니 상품의 수량을 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수량 변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "장바구니 또는 상품을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public org.icd4.commerce.adapter.webapi.common.ApiResponse<Void> updateQuantity(
            @Parameter(description = "장바구니 ID", example = "cart-abc123")
            @PathVariable String cartId,
            @Parameter(description = "아이템 ID", example = "item-xyz789")
            @PathVariable String itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        
        log.info("Updating item quantity. CartId: {}, ItemId: {}, NewQuantity: {}",
            cartId, itemId, request.quantity());
        
        updateCartItemQuantityUseCase.execute(request.toCommand(cartId, itemId));
        
        return org.icd4.commerce.adapter.webapi.common.ApiResponse.success();
    }
    
    /**
     * 장바구니 비우기.
     */
    @DeleteMapping("/{cartId}/items")
    @Operation(summary = "장바구니 비우기", description = "장바구니의 모든 상품을 제거합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "장바구니 비우기 성공"),
        @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public org.icd4.commerce.adapter.webapi.common.ApiResponse<Void> clearCart(
            @Parameter(description = "장바구니 ID", example = "cart-abc123")
            @PathVariable String cartId) {
        
        log.info("Clearing cart: {}", cartId);
        
        ClearCartCommand command = new ClearCartCommand(CartId.of(cartId));
        clearCartUseCase.execute(command);
        
        return org.icd4.commerce.adapter.webapi.common.ApiResponse.success();
    }
    
    /**
     * 장바구니 병합.
     */
    @PostMapping("/{cartId}/merge")
    @Operation(summary = "장바구니 병합", 
        description = "다른 장바구니의 내용을 현재 장바구니로 병합합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "장바구니 병합 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public org.icd4.commerce.adapter.webapi.common.ApiResponse<Void> mergeCarts(
            @Parameter(description = "대상 장바구니 ID (병합 후 유지)", example = "cart-abc123")
            @PathVariable String cartId,
            @Valid @RequestBody MergeCartsRequest request) {
        
        log.info("Merging carts. Target: {}, Source: {}", cartId, request.sourceCartId());
        
        mergeCartsUseCase.execute(request.toCommand(cartId));
        
        return org.icd4.commerce.adapter.webapi.common.ApiResponse.success();
    }
}