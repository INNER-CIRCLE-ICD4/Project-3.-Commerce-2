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
        if (bigDecimal == null || currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("금액과 통화는 필수입니다.");
        }
        return new ProductMoney(bigDecimal, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductMoney that)) return false;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}