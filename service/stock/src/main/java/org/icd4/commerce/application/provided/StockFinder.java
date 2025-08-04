package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.Stock;

public interface StockFinder {
    Stock getStock(String stockId);
    Long checkQuantity(String stockId);
}
