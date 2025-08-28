package org.icd4.commerce.query.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;


import java.util.*;
import java.util.stream.Collectors;

public class ElasticQueryBuilder2 {

    private final String indexName;
    private final List<Query> mustQueries = new ArrayList<>();
    private String sortField;
    private SortOrder sortOrder;

    public ElasticQueryBuilder2(String indexName) {
        this.indexName = indexName;
    }
    public ElasticQueryBuilder2 keyword(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            mustQueries.add(Query.of(q -> q.multiMatch(m -> m
                    .fields("name", "categoryId", "description")
                    .query(keyword).analyzer("korean_analyzer"))));
        }
        return this;
    }

    public ElasticQueryBuilder2 brand(String brand) {
        if (brand != null && !brand.isEmpty()) {
            mustQueries.add(Query.of(q -> q.term(m -> m.field("brand").value(brand))));
        }
        return this;
    }

    public ElasticQueryBuilder2 categoryId(String categoryId) {
        if (categoryId != null && !categoryId.isEmpty()) {
            mustQueries.add(Query.of(q -> q.term(m -> m.field("categoryId").value(categoryId))));
        }
        return this;
    }

    public ElasticQueryBuilder2 price(Integer minPrice, Integer maxPrice) {
        if ((minPrice != null && minPrice > 0) || (maxPrice != null && maxPrice > 0)) {
            NumberRangeQuery.Builder numberRangeQueryBuilder = new NumberRangeQuery.Builder().field("base_price");
            if (minPrice != null && minPrice > 0) {
                numberRangeQueryBuilder.gte(Double.valueOf(minPrice));
            }
            if (maxPrice != null && maxPrice > 0) {
                numberRangeQueryBuilder.lte(Double.valueOf(maxPrice));
            }
            mustQueries.add(Query.of(q -> q.range(
                    r -> r.number(numberRangeQueryBuilder.build())
            )));
        }
        return this;
    }

    // filters=status:activate;attr_color:gray,black;attr_size:s,m
    public ElasticQueryBuilder2 filter(String filters) {
        if (filters != null && !filters.isEmpty()) {
            Map<String, List<String>> variantFilter =  new HashMap<>();
            Map<String, List<String>> variantOptionFilter = new HashMap<>();

            parseFilters(filters, variantFilter, variantOptionFilter);

            buildVariantsQuery(variantFilter);
            buildOptionsQuery(variantOptionFilter);
        }
        return this;
    }

    private void parseFilters(String filters, Map<String, List<String>> variantFilter, Map<String, List<String>> variantOptionFilter) {
        Arrays.stream(filters.split(";"))
                .forEach(filter -> {
                    String[] parts = filter.split(":");
                    if(parts.length == 2) {
                        String key = parts[0];
                        List<String> values = List.of(parts[1].split(","));

                        if(key.startsWith("attr_")) {
                            variantOptionFilter.put(key.substring(5), values);
                        } else {
                            variantFilter.put( key, values);
                        }
                    }
                });
    }

    private void buildVariantsQuery(Map<String, List<String>> var) {
        // satus=activate,inactivate stock=3
        var.forEach((key,values) -> {
            Query termsQuery = createTermOrTermsQuery("variants." + key, values);;
            mustQueries.add(NestedQuery.of(n -> n
                    .path("variants")
                    .query(termsQuery)
            )._toQuery());
        });

    }

    private void buildOptionsQuery(Map<String, List<String>> opt) {
        // attr_color=black,gray attr_size=s,m
        opt.forEach((key,values) -> {
            Query nameQuery = TermQuery.of(t -> t
                    .field("variants.optionCombination.name")
                    .value(key)
            )._toQuery();

            Query valueQuery = createTermOrTermsQuery("variants.optionCombination.value", values);

            mustQueries.add(NestedQuery.of(n -> n
                    .path("variants.optionCombination")
                    .query(q -> q.bool(b -> b.must(nameQuery, valueQuery)))
            )._toQuery());
        });
    }

    private Query createTermOrTermsQuery(String fieldName, List<String> values) {
        if(values.size() == 1) {
            return TermQuery.of(t -> t
                    .field(fieldName)
                    .value(values.get(0))
            )._toQuery();
        } else {
            return TermsQuery.of(t -> t
                    .field(fieldName)
                    .terms(TermsQueryField.of(
                            tq -> tq.value(values.stream()
                                    .map(FieldValue::of)
                                    .collect(Collectors.toList()))
                    ))
            )._toQuery();
        }
    }

    public ElasticQueryBuilder2 sort(String sortField, String sortOrder) {
        if (sortField != null && !sortField.isEmpty()) {
            this.sortField = sortField;
            if ("DESC".equalsIgnoreCase(sortOrder)) {
                this.sortOrder = SortOrder.Desc;

            } else {
                this.sortOrder = SortOrder.Asc;
            }
        }
        return this;
    }
    public SearchRequest buildSearchRequest(int page, int size) {
         SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                .index(indexName)
                .from(page)
                .size(size)
                .query(Query.of(q -> q.bool(BoolQuery.of(b -> b.must(mustQueries)))));

         if(sortField != null && !sortField.isEmpty() && sortOrder != null) {
             searchRequestBuilder.sort(s -> s.field(f -> f
                     .field(sortField)
                     .order(sortOrder)));
         }

        return searchRequestBuilder.build();
    }
}
