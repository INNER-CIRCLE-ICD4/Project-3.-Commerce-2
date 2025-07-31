package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class StockRegisterTest {
    @Autowired
    private StockRegister stockRegister;

    @Test
    void register() {
        Stock stock = stockRegister.register("asfdaf", 3L);

        assertThat(stock.getId()).isNotNull();
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
    }

}