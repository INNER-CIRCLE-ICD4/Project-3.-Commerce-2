package org.icd4.commerce.command.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

// 동기화에 필요한 상품 데이터를 담는 도메인 모델
@Getter
@AllArgsConstructor
public class ProductSync {
    private String id;
    private String name;
    private BigDecimal price;
    // ...
}
