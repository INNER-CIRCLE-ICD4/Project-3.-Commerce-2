package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ReviewResponse;
import org.icd4.commerce.domain.review.ReviewCreateRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewCommandService {
    private final ReviewRegisterService reviewRegisterService;

    public ReviewResponse create(ReviewCreateRequest request) {
        return ReviewResponse.fromDomain(reviewRegisterService.create(request));
    }
}
