package org.icd4.commerce.command.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.JsonData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.ProductCommandRepository;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.icd4.commerce.shared.domain.mapper.ProductDocumentMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductCommandRepository implements ProductCommandRepository {
    private final ElasticsearchClient esClient;
    private final ProductDocumentMapper productDocumentMapper;

    @Transactional
    @Override
    public String createProductDocument(ProductCreateRequest request) throws IOException {
        Product index = productDocumentMapper.toElasticsearchDocument(request);
        IndexRequest<Product> indexRequest = IndexRequest.of(i -> i
                .index("product_index")
                .id(request.productId())
                .document(index)
        );
        IndexResponse response = esClient.index(indexRequest);
        return response.id();
    }

    @Transactional
    @Override
    public String updatePrice(String productId, BigDecimal price, String updateAt) throws IOException {
        UpdateRequest<Product, Object> updateRequest = UpdateRequest.of(u -> u
                .index("product_index")
                .id(productId)
                .doc(Map.of(
                        "basePrice", price,
                        "updatedAt", updateAt
                ))
        );

        UpdateResponse<Product> response = esClient.update(updateRequest, Product.class);
        return response.id();
    }

    // TODO 로직 개선 사항
    @Transactional
    @Override
    public String updateStock(String productId, String sku, int stock, String updateAt) throws IOException {
        UpdateRequest<Product, Object> updateRequest = UpdateRequest.of(u -> u
                .index("product_index")
                .id(productId)
                .script(s -> s
                        .source("for (int i = 0; i < ctx._source.variants.length; i++) { " +
                                "if (ctx._source.variants[i].sku == params.sku) { " +
                                "ctx._source.variants[i].stock = params.newStock; " +
                                "ctx._source.updatedAt = params.updatedAt; " +
                                "break; } }")
                        .params("sku", JsonData.of(sku))
                        .params("newStock", JsonData.of(stock))
                        .params("updatedAt", JsonData.of(updateAt))
                )
        );

        UpdateResponse<Product> response = esClient.update(updateRequest, Product.class);
        return response.id();
    }

    @Transactional
    @Override
    public String updateVariantStatus(String productId, String sku, String variantStatus, String updatedAt) throws IOException {
        UpdateRequest<Product, Object> updateRequest = UpdateRequest.of(u -> u
                .index("product_index")
                .id(productId)
                .script(s -> s
                        .source("for (int i = 0; i < ctx._source.variants.length; i++) { " +
                                "if (ctx._source.variants[i].sku == params.sku) { " +
                                "ctx._source.variants[i].status = params.variantStatus; " +
                                "ctx._source.updatedAt = params.updatedAt; " +
                                "break; } }")
                        .params("sku", JsonData.of(sku))
                        .params("variantStatus", JsonData.of(variantStatus))
                        .params("updatedAt", JsonData.of(updatedAt))
                )
        );

        UpdateResponse<Product> response = esClient.update(updateRequest, Product.class);
        return response.id();
    }

    @Transactional
    @Override
    public String updateStatus(String productId, String status) throws IOException {
        UpdateRequest<Product, Object> updateRequest = UpdateRequest.of(u -> u
                .index("product_index")
                .id(productId)
                .doc(Map.of("status", status))
        );

        UpdateResponse<Product> response = esClient.update(updateRequest, Product.class);
        return response.id();
    }
}
