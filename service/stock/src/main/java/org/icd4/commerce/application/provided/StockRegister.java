package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.Stock;


// transaction ??
// insert, update ??
public interface StockRegister {
    Stock register(String productId, Long quantity);

    void increaseQuantity(String stockId, Long quantity);
    void decreaseQuantity(String stockId, Long quantity);


}
