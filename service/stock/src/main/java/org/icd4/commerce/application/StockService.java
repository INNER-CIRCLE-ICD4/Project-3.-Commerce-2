package org.icd4.commerce.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.persistence.StockJpaRepositoryAdapter;
import org.icd4.commerce.adapter.persistence.StockRedisRepositoryAdapter;
import org.icd4.commerce.application.provided.StockFinder;
import org.icd4.commerce.application.provided.StockRegister;
import org.icd4.commerce.domain.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService implements StockRegister, StockFinder {
    private final StockJpaRepositoryAdapter stockRepositoryAdapter;
    private final StockRedisRepositoryAdapter stockRedisRepositoryAdapter;

    @Override
    public Stock register(String productId, Long quantity) {
        Stock stock = Stock.register(productId, quantity);

        stockRepositoryAdapter.save(stock);

        return stock;
    }

    public Stock registerV2(String productId, Long quantity) {
        Stock stock = Stock.register(productId, quantity);

        stockRedisRepositoryAdapter.save(stock);

        return stock;
    }

    public Stock getStockV2(String stockId) {
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new IllegalArgumentException("재고 ID는 필수입니다.");
        }

        return stockRedisRepositoryAdapter.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId));
    }

    public Long checkQuantityV2(String stockId) {
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new IllegalArgumentException("재고 ID는 필수입니다.");
        }

        return stockRedisRepositoryAdapter.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId))
                .getQuantity();
    }

    @Override
    public Long increaseQuantity(String stockId, Long quantity) {
        return stockRepositoryAdapter.findById(stockId)
                .map(entity -> increaseQuantityAndSave(quantity, entity))
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockId));
    }

    public Long increaseQuantityV1(String stockId, Long quantity) {
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new IllegalArgumentException("재고 ID는 필수입니다.");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }

        int result = stockRepositoryAdapter.increaseStock(stockId, quantity);

        if (result == 0) {
            throw new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId);
        }

        return stockRepositoryAdapter.findById(stockId)
                .orElseThrow().getQuantity();
    }

    public Long decreaseQuantityV1(String stockId, Long quantity) {
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new IllegalArgumentException("재고 ID는 필수입니다.");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }
        int result = stockRepositoryAdapter.decreaseStock(stockId, quantity);

        if (result == 0) {
            if (!stockRepositoryAdapter.findById(stockId).isPresent()) {
                throw new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId);
            } else {
                throw new IllegalArgumentException("재고 수량이 부족합니다. stockId: " + stockId);
            }
        }

        return stockRepositoryAdapter.findById(stockId)
                .orElseThrow().getQuantity();
    }

    public Long increaseQuantityV2(String stockId, Long quantity) {
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new IllegalArgumentException("재고 ID는 필수입니다.");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }

        int result = stockRedisRepositoryAdapter.increaseStock(stockId, quantity);

        if (result == 0) {
            throw new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId);
        }

        return stockRedisRepositoryAdapter.findById(stockId)
                .orElseThrow().getQuantity();
    }

    public Long decreaseQuantityV2(String stockId, Long quantity) {
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new IllegalArgumentException("재고 ID는 필수입니다.");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, quantity);

        if (result == 0) {
            if (!stockRedisRepositoryAdapter.findById(stockId).isPresent()) {
                throw new IllegalArgumentException("재고를 찾을 수 없습니다: " + stockId);
            } else {
                throw new IllegalArgumentException("재고 수량이 부족합니다. stockId: " + stockId);
            }
        }

        return stockRedisRepositoryAdapter.findById(stockId)
                .orElseThrow().getQuantity();
    }


    @Override
    public Long decreaseQuantity(String stockId, Long quantity) {
        return stockRepositoryAdapter.findById(stockId)
                .map(entity -> decreaseQuantityAndSave(quantity, entity))
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockId));

    }

    @Override
    public Stock getStock(String stockId) {
        return stockRepositoryAdapter.findById(stockId)
                .orElseThrow(() -> new NoSuchElementException("재고를 찾을 수 없습니다. stockId: " + stockId));
    }

    @Override
    public Long checkQuantity(String stockId) {
        return stockRepositoryAdapter.findById(stockId)
                .map(Stock::checkQuantity)
                .orElseThrow(() -> new NoSuchElementException("재고를 찾을 수 없습니다. stockId: " + stockId));
    }

    private Long increaseQuantityAndSave(Long quantity, Stock entity) {
        entity.increaseQuantity(quantity);
        Stock stock = stockRepositoryAdapter.save(entity);
        return stock.getQuantity();
    }

    private Long decreaseQuantityAndSave(Long quantity, Stock entity) {
        entity.decreaseQuantity(quantity);
        Stock stock = stockRepositoryAdapter.save(entity);
        return stock.getQuantity();
    }
}
