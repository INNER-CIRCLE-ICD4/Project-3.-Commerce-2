package org.icd4.commerce.adapter.webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.application.command.ProductCommandService;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.Product;
import org.icd4.commerce.domain.product.ProductCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.notNull;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@RequiredArgsConstructor
class ProductApiTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final ProductRepository productRepository;
    final ProductCommandService productCommandService;

    @Test
    void testCreate() throws JsonProcessingException, UnsupportedEncodingException {
        ProductCreateRequest request = new ProductCreateRequest(
                "0001",
                "name",
                "brand",
                "description",
                "0001",
                BigDecimal.ONE,
                "KRW",
                List.of()
        );

        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/api/v1/product")
                .contentType("application/json")
                .content(requestJson).exchange();

        assertThat(result).hasStatus2xxSuccessful()
                .bodyJson()
                .hasPathSatisfying("$.id", notNull());

        ProductResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);
        Product product = productRepository.findById(response.id()).orElseThrow();

        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getBrand()).isEqualTo(request.brand());
        assertThat(product.getDescription()).isEqualTo(request.description());
        assertThat(product.getCategoryId()).isEqualTo(request.categoryId());
        assertThat(product.getPrice().getAmount()).isEqualTo(request.priceAmount());
        assertThat(product.getPrice().getCurrency()).isEqualTo(request.priceCurrency());
    }

}