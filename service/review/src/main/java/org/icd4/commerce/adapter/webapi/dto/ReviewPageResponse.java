package org.icd4.commerce.adapter.webapi.dto;

import java.util.List;

public record ReviewPageResponse(
        List<ReviewResponse> reviews,
        Long reviewCount
) {
    public static ReviewPageResponse of(List<ReviewResponse> reviews, Long reviewCount) {
        return new ReviewPageResponse(reviews, reviewCount);
    }
}
