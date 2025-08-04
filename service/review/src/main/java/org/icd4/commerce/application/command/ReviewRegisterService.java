package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ReviewRegister;
import org.icd4.commerce.application.required.ReviewRepository;
import org.icd4.commerce.domain.review.Review;
import org.icd4.commerce.domain.review.ReviewCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewRegisterService implements ReviewRegister {
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Review create(ReviewCreateRequest request) {
        return reviewRepository.save(Review.create(request));
    }
}
