package org.icd4.commerce.domain.common;

import java.util.Objects;

public record StockKeepingUnit(String value) {
    public StockKeepingUnit {
        Objects.requireNonNull(value, "sku value cannot be null");
    }

    public static StockKeepingUnit of(String value) {
        return new StockKeepingUnit(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockKeepingUnit sku = (StockKeepingUnit) o;
        return Objects.equals(value, sku.value);
    }
}
