package org.icd4.commerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;
import org.icd4.commerce.adapter.webapi.dto.ProductCreateRequest;
import org.icd4.commerce.application.command.ProductCreationCommand;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
public class Product {
    @Id
    private String id;
    private String sellerId;
    private String name;
    private String brand;
    private String description;
    private String categoryId;

    protected Product() {}

    public static Product create(ProductCreateRequest request) {
        Product product = new Product();
        product.id = request.productId();
        product.sellerId = request.sellerId();
        product.name = request.name();
        product.brand = request.brand();
        product.description =request.description();
        product.categoryId = request.categoryId();
        return product;
    }
}
