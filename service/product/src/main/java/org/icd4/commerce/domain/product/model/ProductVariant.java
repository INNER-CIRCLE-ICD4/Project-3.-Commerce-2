package org.icd4.commerce.domain.product.model;

import board.common.dataserializer.DataSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Entity
public class ProductVariant {
    @Id
    private String sku;

    private String productId;
    private String sellerId;

    @Column(columnDefinition = "TEXT")
    private String optionCombination;
    @Embedded
    private ProductMoney sellingPrice;
    private VariantStatus status;
    @Transient
    private Long stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected ProductVariant() {
    }

    public static ProductVariant create(String productId,
                                        String sellerId,
                                        Map<String, String> optionCombination,
                                        ProductMoney sellingPrice,
                                        Long stockQuantity) {
        ProductVariant variant = new ProductVariant();
        variant.sku = generateSku(productId, optionCombination);
        variant.productId = productId;
        variant.sellerId = sellerId;
        variant.optionCombination = DataSerializer.serialize(optionCombination);
        variant.sellingPrice = sellingPrice;
        variant.status = VariantStatus.ACTIVE;
        variant.stockQuantity = stockQuantity;
        variant.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        variant.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
        return variant;
    }

    public static String generateSku(String productId, Map<String, String> options) {
        if (options == null || options.isEmpty()) {
            return productId; // 옵션이 없는 단일 SKU
        }

        String optionHash = options.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "_" + entry.getValue())
                .collect(Collectors.joining("_"));

        return productId + "_" + Integer.toHexString(optionHash.hashCode()).toUpperCase();
    }

    public void updateInfo(ProductVariantUpdateRequest request) {
        updateFieldIfPresent(request.getSellingPrice(), this::updatePrice);
        updateFieldIfPresent(request.stockQuantity(), this::updateStockQuantity);

        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void changeStatus(VariantStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    // 재고 모듈 이벤트 처리


    public boolean isAvailableForPurchase() {
        return this.status == VariantStatus.ACTIVE;
    }

    public Map<String, String> getOptionCombinationMap() {
        if (this.optionCombination == null) {
            return Collections.emptyMap();
        }
        return DataSerializer.deserialize(this.optionCombination, Map.class);
    }

    private <T> void updateFieldIfPresent(T field, Consumer<T> consumer) {
        if (field != null) {
            consumer.accept(field);
        }
    }

    private void updatePrice(ProductMoney price) {
        if (price.getAmount().intValue() <= 0) {
            throw new IllegalArgumentException();
        }
        this.sellingPrice = price;
    }

    private void updateStockQuantity(Long stockQuantity) {
        if (stockQuantity <= 0) {
            throw new IllegalArgumentException();
        }
        this.stockQuantity = stockQuantity;
    }
}
