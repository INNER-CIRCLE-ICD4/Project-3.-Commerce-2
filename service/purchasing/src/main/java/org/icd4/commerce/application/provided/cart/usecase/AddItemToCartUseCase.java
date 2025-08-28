package org.icd4.commerce.application.provided.cart.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.exception.InsufficientStockException;
import org.icd4.commerce.application.provided.cart.command.AddItemToCartCommand;
import org.icd4.commerce.application.required.cart.CartRepositoryPort;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.CartItemLimitExceededException;
import org.icd4.commerce.domain.cart.exception.InvalidQuantityException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니에 상품 추가 유스케이스.
 * 
 * <p>장바구니에 새로운 상품을 추가하거나,
 * 이미 존재하는 상품의 수량을 증가시킵니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddItemToCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    private final InventoryChecker inventoryChecker;
    
    /**
     * 장바구니에 상품을 추가합니다.
     * 
     * @param command 상품 추가 커맨드
     * @throws CartNotFoundException 장바구니를 찾을 수 없는 경우
     * @throws CartAlreadyConvertedException 이미 주문으로 전환된 경우
     * @throws CartItemLimitExceededException 상품 종류가 50개 초과
     * @throws InvalidQuantityException 수량이 유효하지 않은 경우
     */
    @Transactional
    public void execute(AddItemToCartCommand command) {
        log.debug("Adding item to cart. CartId: {}, ProductId: {}, Quantity: {}", 
            command.cartId(), command.productId(), command.quantity());
        
        Cart cart = cartRepository.findById(command.cartId())
            .orElseThrow(() -> new CartNotFoundException(command.cartId()));
        
        // 재고 확인을 위해 현재 장바구니 내 동일 상품/옵션의 수량을 계산합니다.
        int currentQuantityInCart = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(command.productId())
                && item.getOptions().equals(command.options()))
            .mapToInt(item -> item.getQuantity())
            .sum();

        int totalRequestedQuantity = currentQuantityInCart + command.quantity();
        int availableStock = inventoryChecker.getAvailableStock(command.sku()).availableStock();
        if (totalRequestedQuantity > availableStock) {
            throw new InsufficientStockException(
                command.productId(),
                availableStock,
                totalRequestedQuantity
            );
        }
        
        // 도메인 로직 실행 - 수량 증가 또는 신규 추가
        cart.addItem(
            command.productId(),
            command.sku(),
            command.quantity(),
            command.options()
        );
        cartRepository.save(cart);
        
        log.info("Item added to cart successfully. CartId: {}, ProductId: {}, " +
                "TotalItems: {}, TotalQuantity: {}", 
            cart.getId(), command.productId(), 
            cart.getItemCount(), cart.getTotalQuantity());
    }
}