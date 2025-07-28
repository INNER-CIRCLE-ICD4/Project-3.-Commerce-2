package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.product.model.Product;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ProductRepository extends Repository<Product, String> {
    Product save(Product product);

    Optional<Product> findById(String productId);

    void deleteById(String productId);
}
