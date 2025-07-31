package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.Stock;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * 재고 정보를 저장하거나 조회한다.
 */
public interface StockRepository extends Repository<Stock, String> {
    Stock save(Stock stock);

    Optional<Stock> findById(String stockId);
}
