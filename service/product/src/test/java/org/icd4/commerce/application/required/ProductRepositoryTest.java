package org.icd4.commerce.application.required;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icd4.commerce.domain.product.model.ProductStatus.ACTIVE;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void createProduct() {
        var product = Product.create(
                new ProductCreateRequest(
                        "sellerId",
                        "0001",
                        "name",
                        "brand",
                        "description",
                        BigDecimal.valueOf(1000),
                        Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                        List.of(new ProductVariantRequest(
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
                                ))
                ));
        assertThat(product.getId()).isNull();
        productRepository.save(product);

        assertThat(product.getId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        var find = productRepository.findById(product.getId()).orElseThrow();
        assertThat(find.getStatus()).isEqualTo(ACTIVE);
        assertThat(find.getCreatedAt()).isNotNull();
    }
}