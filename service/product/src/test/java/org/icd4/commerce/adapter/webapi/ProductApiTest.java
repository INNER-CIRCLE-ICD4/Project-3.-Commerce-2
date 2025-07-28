
package org.icd4.commerce.adapter.webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icd4.commerce.AssertThatUtils.notNull;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
class ProductApiTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final ProductRepository productRepository;

    @Test
    void testCreateSuccess() throws JsonProcessingException, UnsupportedEncodingException {
        ProductCreateRequest request = createValidProductRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
                .uri("/api/v1/product")
                .contentType("application/json")
                .content(requestJson)
                .exchange();

        assertThat(result)
                .hasStatus(CREATED)
                .bodyJson()
                .hasPathSatisfying("$.id", id -> assertThat(id).isNotNull())
                .hasPathSatisfying("$.id", notNull())
                .hasPath("$.name")
                .hasPath("$.brand")
                .hasPath("$.description")
                .hasPath("$.categoryId")
                .hasPath("$.priceAmount")
                .hasPath("$.priceCurrency");

        ProductResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ProductResponse.class
        );

        Product savedProduct = productRepository.findById(response.id()).orElseThrow();
        assertThat(savedProduct.getName()).isEqualTo(request.name());
        assertThat(savedProduct.getBrand()).isEqualTo(request.brand());
        assertThat(savedProduct.getDescription()).isEqualTo(request.description());
        assertThat(savedProduct.getCategoryId()).isEqualTo(request.categoryId());
//        assertThat(savedProduct.getPrice().getAmount()).isEqualTo(request.priceAmount());
//        assertThat(savedProduct.getPrice().getCurrency()).isEqualTo(request.priceCurrency());
        assertThat(savedProduct.getCreatedAt()).isNotNull();
    }

    @Test
    void testCreateWithInvalidData() throws JsonProcessingException {
        ProductCreateRequest invalidRequest = new ProductCreateRequest(
                null, // sellerId 누락
                "name",
                "brand",
                "description",
                "0001",
                BigDecimal.ONE,
                "KRW",
                List.of()
        );
        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        assertThat(mvcTester.post()
                .uri("/api/v1/product")
                .contentType("application/json")
                .content(requestJson)
                .exchange())
                .hasStatus(BAD_REQUEST);
    }

    @Test
    void testCreateWithInvalidJson() {
        String invalidJson = "{ invalid json }";

        assertThat(mvcTester.post()
                .uri("/api/v1/product")
                .contentType("application/json")
                .content(invalidJson)
                .exchange())
                .hasStatus(BAD_REQUEST);
    }

    private ProductCreateRequest createValidProductRequest() {
        return new ProductCreateRequest(
                "seller123",
                "테스트 상품",
                "테스트 브랜드",
                "상품 설명입니다",
                "category001",
                BigDecimal.valueOf(25000),
                "KRW",
                List.of()
        );
    }
}