package org.icd4.commerce.domain.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCategoryUpdateRequest(
        @NotBlank @NotNull String categoryId
) {
}
