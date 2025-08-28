package org.icd4.commerce.domain.common;

import java.util.Objects;

public record ProductId(String value) {
    public ProductId {
        Objects.requireNonNull(value, "ProductId value cannot be null");
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }

    @Override
    public String toString() {
        return value;
    }
}