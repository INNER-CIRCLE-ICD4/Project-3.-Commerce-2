package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.domain.cart.CartId;

/**
 * 장바구니 병합 커맨드.
 * 
 * @param targetCartId 병합 대상 장바구니 (병합 후 유지됨)
 * @param sourceCartId 병합할 장바구니 (병합 후 삭제 가능)
 * @param deleteSourceCart 병합 후 소스 장바구니 삭제 여부
 */
public record MergeCartsCommand(
    CartId targetCartId,
    CartId sourceCartId,
    boolean deleteSourceCart
) {
    public MergeCartsCommand {
        if (targetCartId == null) {
            throw new IllegalArgumentException("TargetCartId cannot be null");
        }
        if (sourceCartId == null) {
            throw new IllegalArgumentException("SourceCartId cannot be null");
        }
        if (targetCartId.equals(sourceCartId)) {
            throw new IllegalArgumentException("Cannot merge cart with itself");
        }
    }
}