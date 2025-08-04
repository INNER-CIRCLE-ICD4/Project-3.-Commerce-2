package org.icd4.commerce.adapter.webapi;

import org.icd4.commerce.adapter.webapi.dto.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("IllegalArgumentException 처리 - 비즈니스 로직 검증 실패")
    void handleIllegalArgumentException() {
        // Given
        String errorMessage = "재고의 값은 0 이하가 될 수 없습니다.";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("NoSuchElementException 처리 - 엔티티 조회 실패")
    void handleNoSuchElementException() {
        // Given
        String errorMessage = "재고를 찾을 수 없습니다. stockId: test-stock-id";
        NoSuchElementException exception = new NoSuchElementException(errorMessage);

        // When
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleNoSuchElementException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 - Bean Validation 실패")
    void handleMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("stockRegisterRequest", "quantity", "수량은 0보다 커야 합니다.");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        // When
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("수량은 0보다 커야 합니다.");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 - FieldError가 null인 경우")
    void handleMethodArgumentNotValidException_NullFieldError() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(null);

        // When
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("입력값이 올바르지 않습니다.");
        assertThat(response.getBody().getData()).isNull();
    }


} 