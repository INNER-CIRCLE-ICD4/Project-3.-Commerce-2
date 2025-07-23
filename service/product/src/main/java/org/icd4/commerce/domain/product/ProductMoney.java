package org.icd4.commerce.domain.product;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;

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

}