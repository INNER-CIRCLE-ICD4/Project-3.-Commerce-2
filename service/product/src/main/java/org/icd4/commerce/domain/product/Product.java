package org.icd4.commerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;
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
    private String id; // long 자동증가 -> 분산환경에서 중복될 가능성 있어서 UUID 형식으로 변경
    private String sellerId;
    private String name;
    private String brand;
    private String description;
    private String categoryId;
    @Embedded
    private ProductMoney price;
    @ElementCollection
    private List<ProductOption> options = new ArrayList<>();
    private ProductStatus status;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Product() {}

    public static Product create(ProductCreationCommand command) {
        Product product = new Product();
        product.id = UUID.randomUUID().toString();
        product.sellerId = Objects.requireNonNull(command.sellerId(), "Seller ID is required");
        product.name = Objects.requireNonNull(command.name(), "Product name is required");
        product.brand = Objects.requireNonNull(command.brand(), "Product brand is required");
        product.description = Objects.requireNonNull(command.description(), "Product description is required");
        product.categoryId = Objects.requireNonNull(command.categoryId(), "Category ID is required");
        product.price = Objects.requireNonNull(command.price(), "Product price is required");
        product.options = command.options() != null ? new ArrayList<>(command.options()) : new ArrayList<>();
        product.status = ProductStatus.ON_SALE;
        product.isDeleted = false;
        product.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        product.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
        return product;
    }

}
