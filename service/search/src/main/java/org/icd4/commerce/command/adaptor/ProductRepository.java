package org.icd4.commerce.command.adaptor;

import org.icd4.commerce.shared.domain.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product,String> {

}
