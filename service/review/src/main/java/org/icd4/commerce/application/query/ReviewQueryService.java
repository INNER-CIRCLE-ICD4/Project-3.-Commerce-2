package org.icd4.commerce.application.query;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ReviewPageResponse;
import org.icd4.commerce.adapter.webapi.dto.ReviewResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {
    private final ReviewFinderService reviewFinderService;

    public ReviewPageResponse findAll(String productId, String sku, Long page, Long pageSize) {
        List<ReviewResponse> responseList =
                reviewFinderService.findByProductIdAndSku(productId, sku, page, pageSize)
                        .stream()
                        .map(ReviewResponse::fromDomain).toList();
        Long reviewCount = reviewFinderService.count(productId, page, pageSize);
        return ReviewPageResponse.of(responseList, reviewCount);
    }

}
