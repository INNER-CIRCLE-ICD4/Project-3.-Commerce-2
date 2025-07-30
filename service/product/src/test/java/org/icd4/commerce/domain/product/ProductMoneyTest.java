package org.icd4.commerce.domain.product;

import org.icd4.commerce.domain.product.model.ProductMoney;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductMoneyTest {
    @Test
    void equality() {
        var option1 = new ProductMoney(BigDecimal.ONE, Currency.getInstance("KRW").getCurrencyCode());
        var option2 = new ProductMoney(BigDecimal.ONE, Currency.getInstance("KRW").getCurrencyCode());
        assertEquals(option1, option2);
    }
}