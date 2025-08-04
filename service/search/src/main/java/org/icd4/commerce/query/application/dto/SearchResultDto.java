package org.icd4.commerce.query.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

// 검색 결과 담아서 사용자에게 전달 dto
@Getter
@AllArgsConstructor
public class SearchResultDto {
    private String id;
    private String name;
    private BigDecimal price;

    //...
}
