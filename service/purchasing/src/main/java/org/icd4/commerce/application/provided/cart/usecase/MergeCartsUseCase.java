package org.icd4.commerce.application.provided.cart.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.command.MergeCartsCommand;
import org.icd4.commerce.application.required.cart.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.CartItemLimitExceededException;
import org.icd4.commerce.domain.cart.exception.InvalidCartStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니 병합 유스케이스.
 * 
 * <p>두 개의 장바구니를 하나로 병합합니다.
 * 주로 비회원이 로그인할 때 임시 장바구니를 회원 장바구니로
 * 병합하는 경우에 사용됩니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MergeCartsUseCase {
    
    private final CartRepositoryPort cartRepository;
    
    /**
     * 두 장바구니를 병합합니다.
     * 
     * @param command 병합 커맨드
     * @throws CartNotFoundException 장바구니를 찾을 수 없는 경우
     * @throws CartAlreadyConvertedException 이미 주문으로 전환된 경우
     * @throws InvalidCartStateException 병합 대상이 이미 전환된 경우
     * @throws CartItemLimitExceededException 병합 시 상품 종류가 50개 초과
     */
    @Transactional
    public void execute(MergeCartsCommand command) {
        log.debug("Merging carts. Target: {}, Source: {}", 
            command.targetCartId(), command.sourceCartId());
        
        Cart targetCart = cartRepository.findById(command.targetCartId())
            .orElseThrow(() -> new CartNotFoundException(command.targetCartId()));
        
        Cart sourceCart = cartRepository.findById(command.sourceCartId())
            .orElseThrow(() -> new CartNotFoundException(command.sourceCartId()));
        
        int sourceItemCount = sourceCart.getItemCount();
        
        // 도메인 로직 실행
        targetCart.merge(sourceCart);
        
        // 변경사항 저장
        cartRepository.save(targetCart);
        
        // 소스 장바구니 처리
        if (command.deleteSourceCart()) {
            cartRepository.deleteById(sourceCart.getId());
            log.info("Source cart deleted after merge. CartId: {}", sourceCart.getId());
        } else {
            // 소스 장바구니를 비움
            sourceCart.clear();
            cartRepository.save(sourceCart);
        }
        
        log.info("Carts merged successfully. TargetCart: {}, SourceCart: {}, " +
                "MergedItems: {}, TargetTotalItems: {}", 
            targetCart.getId(), sourceCart.getId(), 
            sourceItemCount, targetCart.getItemCount());
    }
}