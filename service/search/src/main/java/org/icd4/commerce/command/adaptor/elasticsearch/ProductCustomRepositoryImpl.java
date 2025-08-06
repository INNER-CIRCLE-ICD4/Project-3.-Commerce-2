package org.icd4.commerce.command.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.ProductCommandCustomRepository;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;

@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImpl implements ProductCommandCustomRepository {
    private final ElasticsearchClient esClient;

    @Override
    public String registerProduct(Product product) {
        try {
            IndexRequest<Product> request = IndexRequest.of(i -> i
                    .index("product_index")
                    .id(product.getId())
                    .document(product));
            IndexResponse response = esClient.index(request);
            return response.id();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        try {
            UpdateRequest<Product, Product> updateRequest = UpdateRequest.of(ur -> ur
                    .index("product_index")
                    .id(product.getId())
                    .doc(product));
            esClient.update(updateRequest, Product.class);
        } catch (IOException e) {
            throw new RuntimeException("상품 전체 업데이트 실패: " + product.getId(), e);
        }
    }

    @Override
    public void updateProductPrice(String productId, BigDecimal newPrice) {
        try {
            UpdateRequest<Product, BigDecimal> updateRequest = UpdateRequest.of(ur -> ur
                    .index("product_index")
                    .id(productId)
                    .doc(newPrice));
            esClient.update(updateRequest, Product.class);
        } catch (IOException e) {
            throw new RuntimeException("상품 기본 가격 업데이트 실패: " + productId, e);
        }
    }

    @Override
    public void updateProductStatus(String productId, String status) {
        try {
            UpdateRequest<Product, String> updateRequest = UpdateRequest.of(ur -> ur
                    .index("product_index")
                    .id(productId)
                    .doc(status));
            esClient.update(updateRequest, Product.class);
        } catch (IOException e) {
            throw new RuntimeException("상품 상태 업데이트 실패: " + productId, e);
        }
    }

    @Override
    public void updateProductVariantPrice(String productId, String sku, BigDecimal newPrice) {

    }

    @Override
    public void updateProductVariantStock(String productId, String sku, Integer newStock) {

    }


}
