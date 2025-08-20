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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductCommandRepository implements ProductCommandRepository {
    private final ElasticsearchClient esClient;

    @Transactional
    @Override
    public String createProductDocument(ProductCreateRequest request) throws IOException {
        Product index = createProduct(request);
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
    public String updateVariantStatus(String productId, String sku, String variantStatus, String updateAt) throws IOException {
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
                        .params("updatedAt", JsonData.of(updateAt))
                )
        );

        UpdateResponse<Product> response = esClient.update(updateRequest, Product.class);
        return response.id();
    }

    private Product createProduct(ProductCreateRequest request) {
        request.autoCompleteSuggestions().addAll(generateSuggestions(request));
        return request.toProduct();
    }

    private List<String> generateSuggestions(ProductCreateRequest request) {
        List<String> suggestions = new ArrayList<>();

        if (request.name() != null) {
            suggestions.add(request.name());
            suggestions.addAll(Arrays.asList(request.name().split("\\s+")));
        }

        if (request.brand() != null) {
            suggestions.add(request.brand());
            suggestions.add(request.brand() + " " + request.name());
        }

        return suggestions.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
