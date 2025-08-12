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

class StockRegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("정상적인 재고 등록 요청 생성")
    void createValidStockRegisterRequest() {
        // Given & When
        StockRegisterRequest request = new StockRegisterRequest("PRODUCT-001", 100L);

        // Then
        assertThat(request.getProductId()).isEqualTo("PRODUCT-001");
        assertThat(request.getQuantity()).isEqualTo(100L);

        Set<ConstraintViolation<StockRegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("상품 ID가 null인 경우 validation 실패")
    void validateFailWhenProductIdIsNull() {
        // Given
        StockRegisterRequest request = new StockRegisterRequest(null, 100L);

        // When
        Set<ConstraintViolation<StockRegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockRegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("productId");
        assertThat(violation.getMessage()).isEqualTo("상품 ID는 필수 입니다.");
    }

    @Test
    @DisplayName("상품 ID가 빈 문자열인 경우 validation 실패")
    void validateFailWhenProductIdIsBlank() {
        // Given
        StockRegisterRequest request = new StockRegisterRequest("", 100L);

        // When
        Set<ConstraintViolation<StockRegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockRegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("productId");
        assertThat(violation.getMessage()).isEqualTo("상품 ID는 필수 입니다.");
    }

    @Test
    @DisplayName("수량이 null인 경우 validation 실패")
    void validateFailWhenQuantityIsNull() {
        // Given
        StockRegisterRequest request = new StockRegisterRequest("PRODUCT-001", null);

        // When
        Set<ConstraintViolation<StockRegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockRegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        assertThat(violation.getMessage()).isEqualTo("수량은 필수입니다.");
    }

    @Test
    @DisplayName("수량이 0인 경우 validation 실패")
    void validateFailWhenQuantityIsZero() {
        // Given
        StockRegisterRequest request = new StockRegisterRequest("PRODUCT-001", 0L);

        // When
        Set<ConstraintViolation<StockRegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockRegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        assertThat(violation.getMessage()).isEqualTo("수량은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("수량이 음수인 경우 validation 실패")
    void validateFailWhenQuantityIsNegative() {
        // Given
        StockRegisterRequest request = new StockRegisterRequest("PRODUCT-001", -10L);

        // When
        Set<ConstraintViolation<StockRegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<StockRegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("quantity");
        assertThat(violation.getMessage()).isEqualTo("수량은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("NoArgsConstructor로 객체 생성 가능")
    void createWithNoArgsConstructor() {
        // Given & When
        StockRegisterRequest request = new StockRegisterRequest();

        // Then
        assertThat(request).isNotNull();
        assertThat(request.getProductId()).isNull();
        assertThat(request.getQuantity()).isNull();
    }
} 