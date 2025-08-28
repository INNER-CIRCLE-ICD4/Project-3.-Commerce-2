package org.icd4.commerce.application.required.common;

import org.icd4.commerce.domain.common.StockKeepingUnit;

public interface InventoryChecker {

    AvailableStock getAvailableStock(StockKeepingUnit sku);

    record AvailableStock(int availableStock) {
        public boolean isAvailable(int quantity) {
            return availableStock >= quantity;
        }
    }
}