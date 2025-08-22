package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.Stock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockRepository {
    Stock save(Stock stock);
    Optional<Stock> findById(String stockId);

    int increaseStock(String stockId, Long quantity);

    int decreaseStock(String stockId, Long quantity);

}
