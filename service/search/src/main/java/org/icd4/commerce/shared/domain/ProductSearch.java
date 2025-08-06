package org.icd4.commerce.shared.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

record ProductSearch(
        String name,
        String brand,
        String description,
        String categoryId
) {

}
