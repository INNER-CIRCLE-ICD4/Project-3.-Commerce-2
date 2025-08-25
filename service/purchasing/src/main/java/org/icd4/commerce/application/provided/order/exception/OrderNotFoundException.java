package org.icd4.commerce.application.provided.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("주문을 찾을 수 없습니다. 주문 ID = " + id);
    }
}
