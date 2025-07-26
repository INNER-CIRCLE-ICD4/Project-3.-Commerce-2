package org.icd4.commerce.application.required;

import org.icd4.commerce.domain.product.model.ProductVariant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ProductQueryRepository extends Repository<ProductVariant, String> {
    @Query("SELECT v FROM ProductVariant v WHERE v.sku = :sku")
    Optional<ProductVariant> findBySkuReadOnly(String sku);
}
