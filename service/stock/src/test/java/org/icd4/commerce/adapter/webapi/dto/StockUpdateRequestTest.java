package org.icd4.commerce.adapter.webapi.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StockUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("정상적인 재고 업데이트 요청 생성")
    void createValidStockUpdateRequest() {
        // Given & When
        StockUpdateRequest request = new StockUpdateRequest(50L);

        // Then
        assertThat(request.getQuantity()).isEqualTo(50L);

        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("수량이 null인 경우 validation 실패")
    void validateFailWhenQuantityIsNull() {
        // Given
        StockUpdateRequest request = new StockUpdateRequest(null);

        // When
        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockUpdateRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        assertThat(violation.getMessage()).isEqualTo("수량은 필수 입니다.");
    }

    @Test
    @DisplayName("수량이 0인 경우 validation 실패")
    void validateFailWhenQuantityIsZero() {
        // Given
        StockUpdateRequest request = new StockUpdateRequest(0L);

        // When
        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockUpdateRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        assertThat(violation.getMessage()).isEqualTo("수량은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("수량이 음수인 경우 validation 실패")
    void validateFailWhenQuantityIsNegative() {
        // Given
        StockUpdateRequest request = new StockUpdateRequest(-10L);

        // When
        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockUpdateRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        assertThat(violation.getMessage()).isEqualTo("수량은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("큰 수량 값으로 요청 생성")
    void createWithLargeQuantity() {
        // Given
        Long largeQuantity = 1_000_000L;

        // When
        StockUpdateRequest request = new StockUpdateRequest(largeQuantity);
        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(request.getQuantity()).isEqualTo(largeQuantity);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("NoArgsConstructor로 객체 생성 가능")
    void createWithNoArgsConstructor() {
        // Given & When
        StockUpdateRequest request = new StockUpdateRequest();

        // Then
        assertThat(request).isNotNull();
        assertThat(request.getQuantity()).isNull();
    }

    @Test
    @DisplayName("1단위 증가/감소 요청")
    void createWithSingleUnit() {
        // Given & When
        StockUpdateRequest request = new StockUpdateRequest(1L);
        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(request.getQuantity()).isEqualTo(1L);
        assertThat(violations).isEmpty();
    }
} 