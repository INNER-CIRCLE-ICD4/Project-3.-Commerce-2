package org.icd4.commerce.domain;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProductFixture {
    public static ProductCreateRequest createProductRequest() {
        return new ProductCreateRequest(
                "sellerId",
                "0001",
                "name",
                "brand",
                "description",
                BigDecimal.valueOf(1000),
                Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                List.of(
                        new ProductVariantRequest(
                                """
                                        {
                                            "optionName": "option1",
                                            "optionValue": "value1"
                                        }
                                        """,
                                BigDecimal.valueOf(1000),
                                Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                                1000L
                        ),
                        new ProductVariantRequest(
                                """
                                        {
                                            "optionName": "option2",
                                            "optionValue": "value2"
                                        }
                                        """,
                                BigDecimal.valueOf(2000),
                                Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                                1000L
                        )
                )
        );
    }

    public static Product createProduct(String productId) {
        Product product = Product.create(createProductRequest());
        ReflectionTestUtils.setField(product, "id", productId);;
        return product;
    }
    public static Product createProduct() {
        return Product.create(createProductRequest());
    }

    public static Product createProductWithVariant(String productId) {
        Product product = Product.create(createProductRequest());
        ReflectionTestUtils.setField(product, "id", productId);
        product.addVariants( List.of(
                new ProductVariantRequest(
                        """
                                {
                                    "optionName": "option1",
                                    "optionValue": "value1"
                                }
                                """,
                        BigDecimal.valueOf(1000),
                        Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                        1000L
                ),
                new ProductVariantRequest(
                        """
                                {
                                    "optionName": "option2",
                                    "optionValue": "value2"
                                }
                                """,
                        BigDecimal.valueOf(2000),
                        Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                        1000L
                )
        ));
        return product;
    }
}
