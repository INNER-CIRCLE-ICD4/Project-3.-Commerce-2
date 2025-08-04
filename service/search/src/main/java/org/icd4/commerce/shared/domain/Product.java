package org.icd4.commerce.shared.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Document(indexName = "product_index")
public class Product {
    @Id
    String productId;
    String sellerId;
    String categoryId;
    String name;
    String brand;
    String description;
    BigDecimal price;
    /************************/
    // String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    // Boolean isDeleted;
}
