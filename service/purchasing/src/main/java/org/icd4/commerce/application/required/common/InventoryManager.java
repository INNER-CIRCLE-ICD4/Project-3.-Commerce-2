package org.icd4.commerce.application.required.common;

import org.icd4.commerce.adapter.external.exception.NotEnoughStockException;
import org.icd4.commerce.adapter.external.exception.ProductServiceException;
import org.icd4.commerce.adapter.external.exception.StockNotFoundException;
import org.icd4.commerce.domain.common.StockKeepingUnit;

/**
 * 재고 차감 인터페이스.
 *
 * <p>결제 성공 시 재고를 차감하기 위한 포트입니다.</p>
 */
public interface InventoryManager {
    String reduceStock(StockKeepingUnit sku, int quantity);
    String restoreStock(StockKeepingUnit sku, int quantity);
}
