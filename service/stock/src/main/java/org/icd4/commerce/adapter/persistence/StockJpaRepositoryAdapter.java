package org.icd4.commerce.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockJpaRepositoryAdapter implements StockRepository {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock save(Stock stock) {
        return stockJpaRepository.save(stock);
    }

    @Override
    public Optional<Stock> findById(String stockId) {
        return stockJpaRepository.findById(stockId);
    }

    @Override
    public int increaseStock(String stockId, Long quantity) {
        return stockJpaRepository.increaseStock(stockId, quantity);
    }

    @Override
    public int decreaseStock(String stockId, Long quantity) {
         return stockJpaRepository.decreaseStock(stockId, quantity);
    }

}
