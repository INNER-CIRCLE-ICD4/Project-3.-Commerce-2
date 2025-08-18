package org.icd4.commerce.query.adaptor;

import org.icd4.commerce.shared.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product,String> {
    // 1. 키워드만 검색 (메서드 기반 쿼리)
    List<Product> findByNameOrBrandOrDescriptionContaining(String keyword, Pageable pageable);

    // 2. 카테고리만 검색 (메서드 기반 쿼리)
    List<Product> findByCategoryId(String categoryId, Pageable pageable);

    /*

    3. 복합 검색 (@Query 어노테이션 사용)

    ?0 : 첫번째 파라미터, ?1 : 두번째 파라미터 ..
    bool: 여러개의 쿼리절 결합
     must: 반드시 참이어야함
     - multi_match : ?0 (첫번째 파라미터) , best_fields: 가장 높은 점수를 받은 필드 점수를 최종 점수로 사용
     filter : 반드시 참이어야함
     - term : 정확히 일치하는지
       categoryId: "chicken salad" -> "chicken", "salad"
       categoryId.keyword : "chicken salad" 한단어로 인식
     - range : gte (>=), lte(<=)

     */
    @Query("""
        {
          "bool": {
            "must": [
              {
                "multi_match": {
                  "query": "?0",
                  "fields": ["name", "brand", "description"],
                  "type": "best_fields"
                }
              }
            ],
            "filter": [
              {
                "term": {
                  "categoryId.keyword": {
                    "value": "?1"
                  }
                }
              },
              {
                "range": {
                  "basePrice": {
                    "gte": ?2,
                    "lte": ?3
                  }
                }
              }
            ]
          }
        }
        """)
    List<Product> searchAdvanced(String keyword, String categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

}
