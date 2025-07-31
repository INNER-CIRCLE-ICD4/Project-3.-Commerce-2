package org.icd4.commerce.application.required;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StockRepositoryTest {
    @Autowired
    StockRepository stockRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void createStock() {
        Stock stock = Stock.register("asdfeaf", 1L);

        stockRepository.save(stock);

        assertThat(stock.getId()).isNotNull();

        entityManager.flush();
    }

}