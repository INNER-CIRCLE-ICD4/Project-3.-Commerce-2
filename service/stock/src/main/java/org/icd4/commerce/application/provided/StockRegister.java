package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.Stock;

public interface StockRegister {
    Stock register(String productId, Long quantity);
    Long increaseQuantity(String stockId, Long quantity);
    Long decreaseQuantity(String stockId, Long quantity);


}
