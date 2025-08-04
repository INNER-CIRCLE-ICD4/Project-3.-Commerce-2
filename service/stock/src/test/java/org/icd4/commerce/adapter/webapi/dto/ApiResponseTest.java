package org.icd4.commerce.adapter.webapi.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("성공 응답 생성 - 데이터 포함")
    void createSuccessResponseWithData() {
        // Given
        String message = "작업이 성공적으로 완료되었습니다.";
        String data = "test-data";

        // When
        ApiResponse<String> response = ApiResponse.success(message, data);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isEqualTo(data);
    }

    @Test
    @DisplayName("성공 응답 생성 - 데이터 없음")
    void createSuccessResponseWithoutData() {
        // Given
        String message = "작업이 성공적으로 완료되었습니다.";

        // When
        ApiResponse<Void> response = ApiResponse.success(message, null);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("실패 응답 생성")
    void createErrorResponse() {
        // Given
        String errorMessage = "작업 처리 중 오류가 발생했습니다.";

        // When
        ApiResponse<Void> response = ApiResponse.error(errorMessage);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo(errorMessage);
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("복잡한 객체를 데이터로 하는 성공 응답")
    void createSuccessResponseWithComplexData() {
        // Given
        String message = "재고 조회 성공";
        StockResponse stockData = StockResponse.builder()
                .stockId("test-stock-id")
                .productId("PRODUCT-001")
                .quantity(100L)
                .build();

        // When
        ApiResponse<StockResponse> response = ApiResponse.success(message, stockData);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isEqualTo(stockData);
        assertThat(response.getData().getProductId()).isEqualTo("PRODUCT-001");
        assertThat(response.getData().getQuantity()).isEqualTo(100L);
    }

    @Test
    @DisplayName("빈 메시지로 성공 응답 생성")
    void createSuccessResponseWithEmptyMessage() {
        // Given
        String emptyMessage = "";
        Integer data = 42;

        // When
        ApiResponse<Integer> response = ApiResponse.success(emptyMessage, data);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEmpty();
        assertThat(response.getData()).isEqualTo(42);
    }

    @Test
    @DisplayName("null 메시지로 실패 응답 생성")
    void createErrorResponseWithNullMessage() {
        // Given
        String nullMessage = null;

        // When
        ApiResponse<Void> response = ApiResponse.error(nullMessage);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("다양한 데이터 타입으로 성공 응답 생성")
    void createSuccessResponseWithVariousDataTypes() {
        // Long 타입 데이터
        ApiResponse<Long> longResponse = ApiResponse.success("수량 조회 성공", 150L);
        assertThat(longResponse.isSuccess()).isTrue();
        assertThat(longResponse.getData()).isEqualTo(150L);

        // Boolean 타입 데이터
        ApiResponse<Boolean> booleanResponse = ApiResponse.success("검증 완료", true);
        assertThat(booleanResponse.isSuccess()).isTrue();
        assertThat(booleanResponse.getData()).isTrue();

        // Void 타입 (null 데이터)
        ApiResponse<Void> voidResponse = ApiResponse.success("처리 완료", null);
        assertThat(voidResponse.isSuccess()).isTrue();
        assertThat(voidResponse.getData()).isNull();
    }
} 