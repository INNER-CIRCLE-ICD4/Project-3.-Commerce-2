package org.icd4.commerce.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockRedisRepositoryAdapter implements StockRepository {
    private final StockRedisRepository stockRedisRepository;

    @Override
    public Stock save(Stock stock) {
        // redis에 재고 수량만 저장 (간단한 캐시 형태)
        stockRedisRepository.setStock(stock.getId(), stock.getQuantity());
        return stock;
    }

    @Override
    public Optional<Stock> findById(String stockId) {
        Long quantity = stockRedisRepository.getStock(stockId);
        if (quantity == null) {
            return Optional.empty();
        }
        return Optional.of(Stock.fromRedis(stockId, quantity));
    }

    @Override
    public int increaseStock(String stockId, Long quantity) {
        // Redis에서 현재 재고 조회
        Long currentQuantity = stockRedisRepository.getStock(stockId);
        if (currentQuantity == null) {
            return 0; //재고 존재 안함.
        }

        // Redis에서 증가(원자적 연산)
        Long newQuantity = stockRedisRepository.setStock(stockId, currentQuantity + quantity);
        return 1;
    }

    @Override
    public int decreaseStock(String stockId, Long quantity) {
        // Redis에서 원자적으로 감소
        Long newQuantity = stockRedisRepository.decreaseStock(stockId, quantity);

        // 감소 후 수량이 음수가 되면 원래대로 되돌리고 실패 처리
        if (newQuantity < 0) {
            // 원래 수량으로 되돌리기
            stockRedisRepository.setStock(stockId, newQuantity + quantity);
            return 0; // 재고 부족
        }

        return 1; // 성공적으로 감소됨
    }




}
