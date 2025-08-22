package org.icd4.commerce.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    private String id;

    private String productId;

    private Long quantity;

    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Stock register(String productId, Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("재고의 값은 0 이하가 될 수 없습니다.");
        }

        Stock stock = new Stock();
        stock.id = UUID.randomUUID().toString();
        stock.productId = requireNonNull(productId, "상품 ID를 입력해주세요.");
        stock.quantity = requireNonNull(quantity, "재고를 입력해주세요.");
        stock.stockStatus = StockStatus.AVAILABLE;
        stock.createdAt = LocalDateTime.now();
        stock.updatedAt = LocalDateTime.now();
        return stock;
    }

    public void increaseQuantity(Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("재고는 0 이하의 값이 될 수 없습니다.");
        }
        this.quantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void decreaseQuantity(Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("재고는 0 이하의 값이 될 수 없습니다.");
        }

        if (this.quantity < quantity) {
            throw new IllegalArgumentException("요청한 수량이 현재 재고보다 많습니다. 현재 재고: " + this.quantity);
        }
        this.quantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public Long checkQuantity() {

        return this.quantity;
    }

    public void empty() {
        this.quantity = 0L;
        this.stockStatus = StockStatus.OUT_OF_STOCK;
    }

    public static Stock fromRedis(String stockId, Long quantity) {
        Stock stock = new Stock();
        stock.id = stockId;
        stock.productId = null; // Redis에서는 productId가 필요하지 않음
        stock.quantity = quantity;
        stock.stockStatus = (quantity <= 0) ? StockStatus.OUT_OF_STOCK : StockStatus.AVAILABLE;
        stock.createdAt = LocalDateTime.now();
        stock.updatedAt = LocalDateTime.now();
        return stock;
    }




}
