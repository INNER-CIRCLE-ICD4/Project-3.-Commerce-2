package org.icd4.commerce.adapter.persistence;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public interface StockRedisRepository {
    @NotNull default String key(@NotNull String stockId) {
        return "stock:" + stockId;
    }
    @Nullable Long getStock(@NotNull String stockId);

    @NotNull  Long decreaseStock(@NotNull String stockId, @NotNull Long quantity);

    @NotNull boolean deleteStock(@NotNull String stockId);

    @NotNull
    Long setStock(@NotNull String stockId, @NotNull Long stock);

}
