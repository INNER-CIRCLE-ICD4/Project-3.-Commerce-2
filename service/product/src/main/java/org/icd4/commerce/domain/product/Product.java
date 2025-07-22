package org.icd4.commerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    protected Product() {
    }

    public static Product create(ProductCreateRequest request) {
        Product product = new Product();
        product.sellerId = requireNonNull(request.sellerId(), "Seller ID is required");
        product.name = requireNonNull(request.name(), "Product name is required");
        product.brand = requireNonNull(request.brand(), "Product brand is required");
        product.description = requireNonNull(request.description(), "Product description is required");
        product.categoryId = requireNonNull(request.categoryId(), "Category ID is required");
        product.price = new ProductMoney(request.priceAmount(), request.priceCurrency());
        product.options = request.options().stream().map(ProductOption::of).toList();
        product.status = ProductStatus.ON_SALE;
        product.isDeleted = false;
        product.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        return product;
    }

    public void updateInfo(ProductInfoUpdateRequest request) {
        this.name = requireNonNull(request.name(), "Product name is required");
        this.brand = requireNonNull(request.brand(), "Product brand is required");
        this.description = requireNonNull(request.description(), "Product description is required");
        if (request.priceAmount() != null && request.priceCurrency() != null) {
            this.price = new ProductMoney(request.priceAmount(), request.priceCurrency());
        }
//        product.options = request.options().stream().map(ProductOption::of).toList();

    }

    public void changeCategory(String categoryId) {
    }

    public void changeStatusStopped() {

    }
}
