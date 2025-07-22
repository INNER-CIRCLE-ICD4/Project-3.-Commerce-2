package org.icd4.commerce.domain.product;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class ProductOption {
    private String name;
    private String description;

    protected ProductOption() {
    }

    public ProductOption(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static ProductOption of(String name, String description) {
        return new ProductOption(name, description);
    }

    public static ProductOption of(ProductOptionRequest request) {
        return new ProductOption(request.name(), request.description());

    }
}
