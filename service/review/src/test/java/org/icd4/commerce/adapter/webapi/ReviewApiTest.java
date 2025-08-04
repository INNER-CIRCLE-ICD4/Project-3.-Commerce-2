package org.icd4.commerce.adapter.webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ReviewResponse;
import org.icd4.commerce.application.required.ReviewRepository;
import org.icd4.commerce.domain.review.Review;
import org.icd4.commerce.domain.review.ReviewCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
class ReviewApiTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final ReviewRepository reviewRepository;

    @Test
    void testCreateSuccess() throws JsonProcessingException, UnsupportedEncodingException {
        ReviewCreateRequest request = createValidReviewRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
                .uri("/api/v1/review")
                .contentType("application/json")
                .content(requestJson)
                .exchange();

        assertThat(result)
                .hasStatus(CREATED)
                .bodyJson()
                .hasPathSatisfying("$.reviewId", id -> assertThat(id).isNotNull())
                .hasPath("$.orderId")
                .hasPath("$.productId")
                .hasPath("$.sku")
                .hasPath("$.userId")
                .hasPath("$.title")
                .hasPath("$.content")
                .hasPath("$.rating");

        ReviewResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ReviewResponse.class
        );

        Review savedReview = reviewRepository.findByReviewId(response.reviewId()).orElseThrow();
        assertThat(savedReview.getOrderId()).isEqualTo(request.orderId());
        assertThat(savedReview.getProductId()).isEqualTo(request.productId());
        assertThat(savedReview.getSku()).isEqualTo(request.sku());
        assertThat(savedReview.getUserId()).isEqualTo(request.userId());
        assertThat(savedReview.getTitle()).isEqualTo(request.title());
        assertThat(savedReview.getContent()).isEqualTo(request.content());
        assertThat(savedReview.getRating()).isEqualTo(request.rating());
        assertThat(savedReview.getCreatedAt()).isNotNull();
    }

    @Test
    void testCreateWithInvalidData() throws JsonProcessingException {
        ReviewCreateRequest invalidRequest = new ReviewCreateRequest(
                "orderId",
                null, // productId 누락
                "sku",
                "userId",
                "title",
                "content",
                5
        );
        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        assertThat(mvcTester.post()
                .uri("/api/v1/review")
                .contentType("application/json")
                .content(requestJson)
                .exchange())
                .hasStatus(BAD_REQUEST);
    }

    @Test
    void testCreateWithInvalidJson() {
        String invalidJson = "{ invalid json }";

        assertThat(mvcTester.post()
                .uri("/api/v1/review")
                .contentType("application/json")
                .content(invalidJson)
                .exchange())
                .hasStatus(BAD_REQUEST);
    }

    @Test
    void testFindByProductIdAndSku() throws JsonProcessingException {
        // First create a review
        ReviewCreateRequest request = createValidReviewRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mvcTester.post()
                .uri("/api/v1/review")
                .contentType("application/json")
                .content(requestJson)
                .exchange();

        // Then retrieve it
        MvcTestResult result = mvcTester.get()
                .uri("/api/v1/review/{productId}/{sku}?page=1&size=10",
                     request.productId(), request.sku())
                .exchange();

        assertThat(result)
                .hasStatus(OK)
                .bodyJson()
                .hasPath("$.reviews")
                .hasPath("$.reviewCount");
    }

    private ReviewCreateRequest createValidReviewRequest() {
        return new ReviewCreateRequest(
                "order123",
                "product123",
                "sku123",
                "user123",
                "Great product!",
                "This product exceeded my expectations.",
                5
        );
    }
}