package org.icd4.commerce.application.required;

import org.icd4.commerce.adapter.webapi.dto.ProductResponse;

import java.time.Duration;
import java.util.Optional;

public interface ProductQueryRepository {
    void create(ProductResponse response, Duration ttl);

    void update(ProductResponse response);

    void delete(String productId);

    Optional<ProductResponse> read(String productId);
}
