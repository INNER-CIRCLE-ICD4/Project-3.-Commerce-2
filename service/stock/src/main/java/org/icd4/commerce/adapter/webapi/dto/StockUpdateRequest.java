package org.icd4.commerce.adapter.webapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {

    @NotNull(message = "수량은 필수 입니다.")
    @jakarta.validation.constraints.Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Long quantity;


}
