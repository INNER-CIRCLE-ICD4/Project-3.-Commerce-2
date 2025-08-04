package org.icd4.commerce.query.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

// es에서 데이터 조회할때 사용하는 검샘전용모델
@Getter
@Document(indexName = "product_index")
public class ProductSearch {
    @Id
    private String id;
    private String name;
    private BigDecimal price;
    // ...

}
