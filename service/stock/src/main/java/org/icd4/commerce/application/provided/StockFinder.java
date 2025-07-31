package org.icd4.commerce.application.provided;

// 조회?
// read
public interface StockFinder {
    Long checkQuantity(String stockId);
}
