package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.Stock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import java.util.Optional;

public interface StockRepository extends Repository<Stock, String> {
    Stock save(Stock stock);
    Optional<Stock> findById(String stockId);

    @Modifying(clearAutomatically = true)
    @Query("update Stock s set s.quantity = s.quantity + :quantity, s.updatedAt = local datetime " +
            "where s.id = :stockId")
    Integer increaseStock(String stockId, Long quantity);

    @Modifying(clearAutomatically = true)
    @Query("update Stock s set s.quantity = s.quantity - :quantity, s.updatedAt = local datetime " +
            "where s.id = :stockId and s.quantity >= :quantity")
    Integer decreaseStock(String stockId, Long quantity);


}
