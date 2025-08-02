package org.icd4.commerce.application.provided.cart.command;

import org.icd4.commerce.domain.cart.CustomerId;

/**
 * 장바구니 생성 커맨드.
 * 
 * @param customerId 고객 식별자
 */
public record CreateCartCommand(
    CustomerId customerId
) {
    public CreateCartCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
    }
}