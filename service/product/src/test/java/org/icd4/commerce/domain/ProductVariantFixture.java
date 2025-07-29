package org.icd4.commerce.domain;

import org.icd4.commerce.domain.product.model.ProductVariant;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

public class ProductVariantFixture {
    public static ProductVariantRequest createProductVariantRequest() {
        return new ProductVariantRequest(
                """
                        {
                            "optionName": "option1",
                            "optionValue": "value1"
                        }
                        """,
                BigDecimal.valueOf(1000),
                Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                1000L
        );
    }

    public static ProductVariant createProductVariant(String sku) {
        ProductVariant productVariant = ProductVariant.create("productId", "sellerId", createProductVariantRequest());
        ReflectionTestUtils.setField(productVariant, "sku", sku);
        return productVariant;
    }
}
