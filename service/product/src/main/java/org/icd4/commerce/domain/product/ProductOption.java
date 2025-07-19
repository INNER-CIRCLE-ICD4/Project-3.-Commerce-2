package org.icd4.commerce.domain.product;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class ProductOption {
    private String name;
    private String value;

    protected ProductOption() {}

    public ProductOption(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
