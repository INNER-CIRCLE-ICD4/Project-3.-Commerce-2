package org.icd4.commerce.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.StockFinder;
import org.icd4.commerce.application.provided.StockRegister;
import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
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
    public Long increaseQuantity(String stockId, Long quantity) {
        return stockRepository.findById(stockId)
                .map(entity -> increaseQuantityAndSave(quantity, entity))
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockId));
    }


    @Override
    public Long decreaseQuantity(String stockId, Long quantity) {
        return stockRepository.findById(stockId)
                .map(entity -> decreaseQuantityAndSave(quantity, entity))
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockId));

    }

    @Override
    public Stock getStock(String stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new NoSuchElementException("재고를 찾을 수 없습니다. stockId: " + stockId));
    }

    @Override
    public Long checkQuantity(String stockId) {
        return stockRepository.findById(stockId)
                .map(Stock::checkQuantity)
                .orElseThrow(() -> new NoSuchElementException("재고를 찾을 수 없습니다. stockId: " + stockId));
    }

    private Long increaseQuantityAndSave(Long quantity, Stock entity) {
        entity.increaseQuantity(quantity);
        Stock stock = stockRepository.save(entity);
        return stock.getQuantity();
    }

    private Long decreaseQuantityAndSave(Long quantity, Stock entity) {
        entity.decreaseQuantity(quantity);
        Stock stock = stockRepository.save(entity);
        return stock.getQuantity();
    }
}
