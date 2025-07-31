package org.icd4.commerce.application;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.StockFinder;
import org.icd4.commerce.application.provided.StockRegister;
import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService implements StockRegister, StockFinder {
    private final StockRepository stockRepository;

    @Override
    public Stock register(String productId, Long quantity) {

        Stock stock = Stock.register(productId, quantity);

        stockRepository.save(stock);

        return stock;
    }

    @Override
    public void increaseQuantity(String stockId, Long quantity) {
        Optional<Stock> stock = stockRepository.findById(stockId);

        stock.ifPresent(entity -> {
            entity.increaseQuantity(quantity);

            stockRepository.save(entity);
        });


    }

    @Override
    public void decreaseQuantity(String stockId, Long quantity) {
        Optional<Stock> stock = stockRepository.findById(stockId);

        stock.ifPresent(entity -> {
            entity.decreaseQuantity(quantity);

            stockRepository.save(entity);
        });

    }

    @Override
    public Long checkQuantity(String stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);

        return stock.get().checkQuantity();
    }
}
