package org.icd4.commerce.config;

import org.icd4.commerce.adapter.persistence.StockJpaRepository;
import org.icd4.commerce.adapter.persistence.StockJpaRepositoryAdapter;
import org.icd4.commerce.application.required.StockRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public StockRepository stockRepository(StockJpaRepository stockJpaRepository) {
        return new StockJpaRepositoryAdapter(stockJpaRepository);
    }
}