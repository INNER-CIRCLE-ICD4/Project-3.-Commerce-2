package org.icd4.commerce.adapter.webapi;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ReviewPageResponse;
import org.icd4.commerce.adapter.webapi.dto.ReviewResponse;
import org.icd4.commerce.application.command.ReviewCommandService;
import org.icd4.commerce.application.query.ReviewQueryService;
import org.icd4.commerce.domain.review.ReviewCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewApi {
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @GetMapping("/{productId}/{sku}")
    public ResponseEntity<ReviewPageResponse> findByProductIdAndSku(@PathVariable String productId,
                                                                    @PathVariable String sku,
                                                                    @RequestParam(name = "page", required = false) Long page,
                                                                    @RequestParam(name = "size", required = false) Long size) {
        return ResponseEntity.ok(reviewQueryService.findAll(productId, sku, page, size));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewCommandService.create(request));
    }
}
