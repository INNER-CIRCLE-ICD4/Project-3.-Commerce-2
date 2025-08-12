package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.Stock;
import org.springframework.data.repository.Repository;
import java.util.Optional;

public interface StockRepository extends Repository<Stock, String> {
    Stock save(Stock stock);
    Optional<Stock> findById(String stockId);
}
