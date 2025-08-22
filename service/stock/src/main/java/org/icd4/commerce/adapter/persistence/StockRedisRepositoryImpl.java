package org.icd4.commerce.adapter.persistence;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRedisRepositoryImpl implements StockRedisRepository {
    private final StringRedisTemplate redisTemplate;

    @Nullable
    @Override
    public Long getStock(String stockId) {
        final String stockStr = redisTemplate.opsForValue().get(key(stockId));
        if (stockStr == null) {
            return null;
        }
        return Long.parseLong(stockStr);
    }

    @Override
    public @NotNull Long decreaseStock(@NotNull String stockId, @NotNull Long quantity) {
        return redisTemplate.opsForValue().decrement(key(stockId), quantity);
    }

    @Override
    public @NotNull boolean deleteStock(String stockId) {
        return redisTemplate.delete(key(stockId));
    }

    @Override
    public @NotNull Long setStock(String stockId, Long stock) {
        redisTemplate.opsForValue().set(key(stockId), stock.toString());
        return stock;
    }
}
