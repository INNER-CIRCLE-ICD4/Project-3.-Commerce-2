package org.icd4.commerce.query.adaptor.elasticsearch;

import org.icd4.commerce.query.domain.ProductSearch;
import org.icd4.commerce.query.domain.ProductSearchRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchElasticRepository extends ElasticsearchRepository<ProductSearch, String>, ProductSearchRepository {
    @Override
    List<ProductSearch> searchByKeyword(String keyword);

}
