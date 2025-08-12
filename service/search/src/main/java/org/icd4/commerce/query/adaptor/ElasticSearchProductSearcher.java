package org.icd4.commerce.query.adaptor;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.required.ProductSearcher;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductSearcher implements ProductSearcher {

    private final ElasticsearchClient esClient;
    private static final String INDEX_NAME = "product_index";

    @Override
    public List<Product> searchByKeyword(String keyword) throws IOException {

        // 엘라스틱서치 클라이언트 사용하여 검색 로직 구현
        System.out.println("Searching for keyword: " + keyword);

        // 엘라스틱서치에 보낼 검색 쿼리 생성
        // 이름과, 브랜드 필드에 키워드가 포함돼있는지 검색하는 쿼리
        Query q = Query.of(qq -> qq
                .multiMatch(mm -> mm
                        .fields("name", "brand", "description")
                        .query(keyword)
                )
        );

        // 검색 요청
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                        .query(q)
        );

        // 응답
        // Hit: 각 요소에서 데이터 부분(source)만 추출
        SearchResponse<Product> response = esClient.search(searchRequest, Product.class);
        return response.hits().hits().stream()
         .map(Hit::source)
         .collect(Collectors.toList());

    }

    @Override
    public List<Product> searchByCategory(String categoryId) throws IOException {
        return List.of();
    }

    @Override
    public List<Product> searchWithAdvancedOptions(String keyword, Map<String, Object> filters, String sortField, String sortOrder) throws IOException {
        return List.of();
    }

    @Override
    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        return List.of();
    }
}
