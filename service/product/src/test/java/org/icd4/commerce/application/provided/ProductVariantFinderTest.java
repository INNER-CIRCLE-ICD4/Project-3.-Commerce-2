package org.icd4.commerce.application.provided;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductVariantFinderTest {
    private final ProductVariantFinder productVariantFinder;
    private final EntityManager entityManager;

    ProductVariantFinderTest(ProductVariantFinder productVariantFinder, EntityManager entityManager) {
        this.productVariantFinder = productVariantFinder;
        this.entityManager = entityManager;
    }

    @Test
    void findProductVariantByIdAndSku() {
        //Given
        Product product = createTestProduct();
        String expectedSku = product.getAllVariants().getFirst().getSku();

        //When
        ProductVariant productVariant = productVariantFinder.findProductVariantByIdAndSku(product.getId(),expectedSku);
        entityManager.flush();

        //Then
        assertThat(productVariant.getSku()).isEqualTo(expectedSku);
        assertThat(productVariant.getOptionCombinationMap()).containsExactly(
                entry("optionName", "option1"),
                entry("optionValue", "value1"));
    }

    private Product createTestProduct() {
        ProductCreateRequest request = createTestProductRequest();
        var product = Product.create(request);
        entityManager.persist(product);
        product.addVariants(request.variants());
        entityManager.flush();
        return product;
    }

    private ProductCreateRequest createTestProductRequest() {
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
                        )
                )
        );
    }
}