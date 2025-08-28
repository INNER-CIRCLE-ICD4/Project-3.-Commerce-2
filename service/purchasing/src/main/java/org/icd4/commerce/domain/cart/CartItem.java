package org.icd4.commerce.domain.cart;

import lombok.Getter;
import org.icd4.commerce.domain.cart.exception.InvalidQuantityException;
import org.icd4.commerce.domain.cart.exception.RequiredOptionMissingException;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

@Getter
public class CartItem {
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 99;

    private final CartItemId id;
    private final ProductId productId;
    private final StockKeepingUnit sku;
    private final ProductOptions options;
    private final LocalDateTime addedAt;
    private final TimeProvider timeProvider;
    private int quantity;
    private LocalDateTime lastModifiedAt;
    private boolean isAvailable;
    private String unavailableReason;

    public CartItem(CartItemId id, ProductId productId, StockKeepingUnit sku, ProductOptions options,
                    int quantity, TimeProvider timeProvider) {
        this.id = requireNonNull(id, "CartItemId cannot be null");
        this.productId = requireNonNull(productId, "ProductId cannot be null");
        this.sku = requireNonNull(sku, "StockKeepingUnit cannot be null");
        this.options = requireNonNull(options, "ProductOptions cannot be null");
        this.timeProvider = requireNonNull(timeProvider, "TimeProvider cannot be null");
        validateQuantity(quantity);
        this.quantity = quantity;
        this.addedAt = timeProvider.now();
        this.lastModifiedAt = timeProvider.now();
        this.isAvailable = true;
    }

    public CartItem(CartItemId id, ProductId productId, StockKeepingUnit sku, ProductOptions options,
                    int quantity, LocalDateTime addedAt, LocalDateTime lastModifiedAt,
                    boolean isAvailable, String unavailableReason, TimeProvider timeProvider) {
        this.id = requireNonNull(id, "CartItemId cannot be null");
        this.productId = requireNonNull(productId, "ProductId cannot be null");
        this.sku = requireNonNull(sku, "StockKeepingUnit cannot be null");
        this.options = requireNonNull(options, "ProductOptions cannot be null");
        this.quantity = quantity;
        this.addedAt = requireNonNull(addedAt, "AddedAt cannot be null");
        this.lastModifiedAt = requireNonNull(lastModifiedAt, "LastModifiedAt cannot be null");
        this.isAvailable = isAvailable;
        this.unavailableReason = unavailableReason;
        this.timeProvider = requireNonNull(timeProvider, "TimeProvider cannot be null");
    }

    public void updateQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        this.lastModifiedAt = timeProvider.now();
    }

    public void increaseQuantity(int additionalQuantity) {
        int newQuantity = this.quantity + additionalQuantity;
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        this.lastModifiedAt = timeProvider.now();
    }

    public void markAsUnavailable(String reason) {
        this.isAvailable = false;
        this.unavailableReason = reason;
        this.lastModifiedAt = timeProvider.now();
    }

    public void markAsAvailable() {
        this.isAvailable = true;
        this.unavailableReason = null;
        this.lastModifiedAt = timeProvider.now();
    }

    public boolean isSameProduct(ProductId otherProductId, ProductOptions otherOptions) {
        return this.productId.equals(otherProductId) && this.options.equals(otherOptions);
    }

    private void validateQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new InvalidQuantityException(
                    String.format("Quantity must be between %d and %d, but was %d",
                            MIN_QUANTITY, MAX_QUANTITY, quantity)
            );
        }
    }

    public void validateRequiredOptions(Map<String, Boolean> requiredOptions) {
        for (Map.Entry<String, Boolean> entry : requiredOptions.entrySet()) {
            String optionKey = entry.getKey();
            boolean isRequired = entry.getValue();

            if (isRequired && !options.hasOption(optionKey)) {
                throw new RequiredOptionMissingException(
                        String.format("Required option '%s' is missing", optionKey)
                );
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() {
        return hash(id);
    }
}