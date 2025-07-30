package org.icd4.commerce.domain.product;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Getter
@Document(indexName = "product_index")
public class Product {
    @Id
    String productId;
    String categoryId;
    String name;
    String brand;
    String description;
    /************************/
    // String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    // Boolean isDeleted;
}
