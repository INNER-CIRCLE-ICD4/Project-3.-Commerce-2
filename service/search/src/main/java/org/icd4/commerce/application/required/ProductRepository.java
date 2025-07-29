package org.icd4.commerce.application.required;

import org.icd4.commerce.domain.product.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, String>, ElasticsearchRepository<Product, String> {
}
