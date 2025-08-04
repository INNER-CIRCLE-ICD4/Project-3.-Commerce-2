package org.icd4.commerce.command.domain;

import lombok.Getter;

import java.math.BigDecimal;

// 동기화에 필요한 상품 데이터를 담는 도메인 모델
@Getter
public class Product {
    private String id;
    private String name;
    private BigDecimal price;
    // ...
}
