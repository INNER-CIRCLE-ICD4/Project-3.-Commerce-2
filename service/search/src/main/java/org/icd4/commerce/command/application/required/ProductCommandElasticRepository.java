package org.icd4.commerce.command.application.required;

import org.icd4.commerce.shared.domain.Product;
import org.springframework.data.repository.Repository;

public interface ProductCommandElasticRepository extends Repository<Product, String> {
    void deleteById(String id);
}
