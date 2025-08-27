package org.icd4.commerce.query.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import org.icd4.commerce.shared.utils.FilterParser;

import java.util.List;

public class ElasticQueryBuilder {
    private final BoolQuery.Builder boolQuery = QueryBuilders.bool();
    private final SearchRequest.Builder searchBuilder = new SearchRequest.Builder();
    private final FilterParser filterParser = new FilterParser();

    public ElasticQueryBuilder index(String indexName) {
        searchBuilder.index(indexName);
        return this;
    }

    public ElasticQueryBuilder keyword(String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            boolQuery.must(QueryBuilders.multiMatch()
                    .fields("name", "brand", "description")
                    .query(keyword)
                    .build()._toQuery());
        }
        return this;
    }

    public ElasticQueryBuilder category(String categoryId) {
        if (categoryId != null && !categoryId.isBlank()) {
            boolQuery.filter(QueryBuilders.term()
                    .field("categoryId")
                    .value(categoryId)
                    .build()._toQuery());
        }
        return this;
    }

    public ElasticQueryBuilder filters(String filters) {
        List<String> filterCandidates = filterParser.parseToFlattenedFilters(filters);

        if (!filterCandidates.isEmpty()) {
            boolQuery.filter(QueryBuilders.terms()
                    .field("productAttributes")
                    .terms(TermsQueryField.of(t -> t.value(
                            filterCandidates.stream().map(FieldValue::of).toList())))
                    .build()._toQuery());
        }
        return this;
    }

    public ElasticQueryBuilder sort(String sortField, String sortOrder) {
        if (sortField != null && sortOrder != null) {
            SortOrder order = "desc".equalsIgnoreCase(sortOrder)
                    ? SortOrder.Desc : SortOrder.Asc;
            searchBuilder.sort(SortOptions.of(s -> s
                    .field(FieldSort.of(f -> f
                            .field(sortField)
                            .order(order)))));
        }
        return this;
    }

    public ElasticQueryBuilder page(int page, int size) {
        searchBuilder.from(page * size).size(size);
        return this;
    }

    public SearchRequest build() {
        return searchBuilder.query(boolQuery.build()._toQuery()).build();
    }

}
