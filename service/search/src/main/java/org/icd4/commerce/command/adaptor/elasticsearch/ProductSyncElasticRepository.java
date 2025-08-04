package org.icd4.commerce.command.adaptor.elasticsearch;


import org.icd4.commerce.command.domain.ProductSync;
import org.icd4.commerce.command.domain.ProductSyncRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSyncElasticRepository extends ElasticsearchRepository<ProductSync,String>, ProductSyncRepository {


}
