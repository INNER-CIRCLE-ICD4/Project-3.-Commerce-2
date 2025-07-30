package org.icd4.commerce.application.required;

import org.icd4.commerce.domain.product.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}
