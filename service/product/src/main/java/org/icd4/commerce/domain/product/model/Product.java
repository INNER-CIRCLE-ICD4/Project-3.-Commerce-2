package org.icd4.commerce.domain.product.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String sellerId;
    private String name;
    private String brand;
    private String description;
    private String categoryId;

    @Embedded
    private ProductMoney basePrice;
    private ProductStatus status;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    protected Product() {
    }

    public static Product create(ProductCreateRequest request) {
        Product product = new Product();
        product.sellerId = requireNonNull(request.sellerId());
        product.name = requireNonNull(request.name());
        product.brand = requireNonNull(request.brand());
        product.description = requireNonNull(request.description());
        product.categoryId = requireNonNull(request.categoryId());
        product.basePrice = ProductMoney.of(request.priceAmount(), request.priceCurrency());
        product.status = ProductStatus.ACTIVE;
        product.isDeleted = false;
        product.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        product.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
        return product;
    }

    // ===== 애그리거트 전체 비즈니스 로직 =====
    public void updateInfo(ProductInfoUpdateRequest request) {
        Assert.state(!this.isDeleted, "삭제된 상품은 수정할 수 없습니다.");

        updateFieldIfPresent(request.name(), this::updateName);
        updateFieldIfPresent(request.brand(), this::updateBrand);
        updateFieldIfPresent(request.description(), this::updateDescription);

        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    // on off
    public void activate() {
        Assert.state(ProductStatus.ACTIVE != this.status, "이미 활성화된 상품입니다");
        changeStatus(ProductStatus.ACTIVE);
    }

    public void inactivate() {
        Assert.state(ProductStatus.INACTIVE != this.status, "이미 비활성화된 상품입니다");
        changeStatus(ProductStatus.INACTIVE);
    }

    public void changeCategory(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new IllegalArgumentException("상품 카테고리는 필수값입니다");
        }

        // 추가 도메인 규칙이 있다면
        if (Objects.equals(this.categoryId, categoryId)) {
            throw new IllegalArgumentException("동일한 카테고리로는 변경할 수 없습니다");
        }

        if (this.getIsDeleted()) {
            throw new IllegalStateException();
        }

        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void changePrice(ProductMoney newPrice) {
        if (newPrice.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be a positive value");
        }
        this.basePrice = requireNonNull(newPrice);
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void changeStatus(ProductStatus newStatus) {
        this.status = requireNonNull(newStatus);
        // 상품 상태 변경이 모든 변형에 영향
        if (newStatus == ProductStatus.INACTIVE) {
            variants.forEach(variant -> variant.changeStatus(VariantStatus.INACTIVE));
        }

        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void delete() {
        if (this.status == ProductStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot delete active product");
        }
        if (this.isDeleted) {
            throw new IllegalArgumentException("Product is already deleted");
        }

        variants.forEach(variant -> variant.changeStatus(VariantStatus.DISCONTINUED));
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    // 재고 모듈 이벤트 처리 (애그리거트 루트를 통해서만)
    public void handleStockStatusChanged(String sku) {

    }

    /**
     * 단일 변형 추가
     */
    public ProductVariant addVariant(Map<String, String> optionCombination, ProductMoney sellingPrice, Long stockQuantity) {
        ProductVariant variant = ProductVariant.create(this.id, this.sellerId, optionCombination, sellingPrice, stockQuantity);
        this.variants.add(variant);
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
        return variant;
    }

    /**
     * 벌크 변형 추가 메서드
     */
    public void addVariants(List<ProductVariantRequest> variantRequests) {
        if (this.id == null) {
            throw new IllegalStateException("Product ID가 필요합니다.");
        }
        List<String> newSkus = variantRequests.stream()
                .map(request -> ProductVariant.generateSku(this.id, request.getOptionCombinationMap()))
                .toList();

        validateNoDuplicateSkus(newSkus);

        for (ProductVariantRequest request : variantRequests) {
            ProductVariant variant = ProductVariant.create(
                    this.id,
                    this.sellerId,
                    request.getOptionCombinationMap(),
                    request.getSellingPrice(),
                    request.stockQuantity()
            );
            this.variants.add(variant);
        }
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void updateVariant(String sku, ProductVariantUpdateRequest request) {
        validateVariantUpdate(request);
        ProductVariant variant = findVariantBySku(sku);
        if (variant == null) {
            throw new IllegalArgumentException("SKU를 찾을 수 없습니다: " + sku);
        }
        updateVariantInternal(variant, request);
    }

    public void updateVariantStatus(String sku, VariantStatus status) {
        ProductVariant variant = findVariantBySku(sku);
        Assert.state(variant.getStatus() == status, "현재 상태와 같은 상태로는 변경할 수 없습니다.");
        variant.changeStatus(status);
    }

    public void removeVariant(String sku) {
        ProductVariant variant = findVariantBySku(sku);
        if (variant == null) {
            throw new IllegalArgumentException("SKU를 찾을 수 없습니다: " + sku);
        }

        variant.changeStatus(VariantStatus.DISCONTINUED);
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    // ===== ProductVariant 조회 메서드들 =====
    public ProductVariant findVariantBySku(String sku) {
        return variants.stream()
                .filter(v -> v.getSku().equals(sku))
                .findFirst()
                .orElse(null);
    }

    public List<ProductVariant> getAvailableVariants() {
        return variants.stream()
                .filter(ProductVariant::isAvailableForPurchase)
                .toList();
    }

    public List<ProductVariant> getAllVariants() {
        return Collections.unmodifiableList(variants);
    }

    public boolean hasAvailableVariants() {
        return variants.stream().anyMatch(ProductVariant::isAvailableForPurchase);
    }

    public int getVariantCount() {
        return variants.size();
    }

    public boolean hasVariants() {
        return !variants.isEmpty();
    }

    private void validateNoDuplicateSkus(List<String> newSkus) {
        Set<String> existingSkus = variants.stream()
                .map(ProductVariant::getSku)
                .collect(Collectors.toSet());

        for (String newSku : newSkus) {
            if (existingSkus.contains(newSku)) {
                throw new IllegalArgumentException("중복된 SKU가 존재합니다: " + newSku);
            }
        }
    }

    private void validateVariantUpdate(ProductVariantUpdateRequest request) {
        // 검증
    }

    private void updateVariantInternal(ProductVariant variant, ProductVariantUpdateRequest request) {
        variant.updateInfo(request);
    }

    private <T> void updateFieldIfPresent(T field, Consumer<T> consumer) {
        if (field != null) {
            consumer.accept(field);
        }
    }

    private void updateName(String name) {
        this.name = name;
    }

    private void updateBrand(String brand) {
        this.brand = brand;
    }

    private void updateDescription(String description) {
        this.description = description;
    }
}