package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.product.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends Repository<Product, String> {
    Product save(Product product);

    Optional<Product> findById(String productId);

    void deleteById(String productId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.id = :productId")
    Optional<Product> findByIdWithVariants(@Param("productId") String productId);
}
