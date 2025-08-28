package org.icd4.commerce.application.required.common;

import org.icd4.commerce.adapter.external.exception.NotEnoughStockException;
import org.icd4.commerce.adapter.external.exception.ProductServiceException;
import org.icd4.commerce.adapter.external.exception.StockNotFoundException;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;

/**
 * 재고 차감 인터페이스.
 *
 * <p>결제 성공 시 재고를 차감하기 위한 포트입니다.</p>
 */
public interface InventoryReducer {
    /**
     * 재고 차감 요청
     * @throws StockNotFoundException 재고 없음
     * @throws NotEnoughStockException 수량 부족
     * @throws ProductServiceException 서비스 통신 오류
     */
    String reduceStock(StockKeepingUnit productId, int quantity);
}
