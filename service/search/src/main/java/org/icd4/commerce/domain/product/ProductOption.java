package org.icd4.commerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String value;

    protected ProductOption() {}

    public ProductOption(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
