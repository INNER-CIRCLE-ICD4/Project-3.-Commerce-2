package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.Stock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockRepository extends Repository<Stock, String> {
    Stock save(Stock stock);
    Optional<Stock> findById(String stockId);

    @Modifying(clearAutomatically = true)
    @Query("update Stock s set s.quantity = s.quantity + :quantity, s.updatedAt = CURRENT_TIMESTAMP " +
            "where s.id = :stockId")
    int increaseStock(String stockId, Long quantity);

    @Modifying(clearAutomatically = true)
    @Query("update Stock s set s.quantity = s.quantity - :quantity, s.updatedAt = CURRENT_TIMESTAMP " +
            "where s.id = :stockId and s.quantity >= :quantity")
    int decreaseStock(@Param("stockId") String stockId, @Param("quantity") Long quantity);



}
