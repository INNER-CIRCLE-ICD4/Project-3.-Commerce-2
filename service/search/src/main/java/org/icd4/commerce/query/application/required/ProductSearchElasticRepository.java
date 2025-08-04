package org.icd4.commerce.query.application.required;

import org.icd4.commerce.shared.domain.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchElasticRepository extends ElasticsearchRepository<Product, String> {
    List<Product> findAllByNameAndBrandAndDescriptionAndCategoryIdMatches(String keyword);

}
