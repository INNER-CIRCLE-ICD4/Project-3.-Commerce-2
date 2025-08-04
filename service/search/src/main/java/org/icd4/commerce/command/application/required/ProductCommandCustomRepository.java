package org.icd4.commerce.command.application.required;

import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCommandCustomRepository {
    String registerProduct(Product product);
    void deleteProduct(String productId);
}
