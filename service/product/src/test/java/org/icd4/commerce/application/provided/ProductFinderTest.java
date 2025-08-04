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

@SpringBootTest
@Transactional
class ProductFinderTest {
    private final ProductFinder productFinder;
    private final EntityManager entityManager;

    public ProductFinderTest(ProductFinder productFinder, EntityManager entityManager) {
        this.productFinder = productFinder;
        this.entityManager = entityManager;
    }

    @Test
    void findProductById() {
        //Given
        String productId = createTestProduct().getId();
        //When
        Product product = productFinder.findById(productId);
        entityManager.flush();

        //Then
        assertThat(product.getId()).isEqualTo(productId);
        assertThat(product.getName()).isEqualTo("name");
        assertThat(product.getBrand()).isEqualTo("brand");
        assertThat(product.getDescription()).isEqualTo("description");
        assertThat(product.getBasePrice().getAmount()).isEqualTo(BigDecimal.valueOf(1000));
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