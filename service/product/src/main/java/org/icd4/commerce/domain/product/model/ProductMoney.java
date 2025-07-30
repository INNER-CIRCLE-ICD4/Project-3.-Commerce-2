package org.icd4.commerce.domain.product.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
@Getter
public class ProductMoney {
    private BigDecimal amount;
    private String currency;

    protected ProductMoney() {}

    public ProductMoney(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static ProductMoney of(BigDecimal bigDecimal, String currency) {
        return new ProductMoney(bigDecimal, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductMoney that)) return false;

        return Objects.equals(currency, that.currency)
                && amount != null
                && that.amount != null
                && amount.compareTo(that.amount) == 0;
    }

    @Override
    public int hashCode() {
        BigDecimal normalizedAmount = amount != null ? amount.stripTrailingZeros() : null;
        return Objects.hash(normalizedAmount, currency);
    }

}