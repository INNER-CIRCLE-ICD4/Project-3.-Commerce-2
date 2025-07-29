package org.icd4.commerce.domain.product;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Document(indexName = "product")
public class ProductIndex {
    @Id
    String productId;
    String categoryId;
    String name;
    String brand;
    String description;
}
