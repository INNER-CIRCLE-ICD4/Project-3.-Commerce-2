package org.icd4.commerce.domain.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductOptionTest {

    @Test
    void equality() {
        var option1 = new ProductOption("Color", "Red");
        var option2 = new ProductOption("Size", "XL");
        assertEquals(option1, option2);
    }

}