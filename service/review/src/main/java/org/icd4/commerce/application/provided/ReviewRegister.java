package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.review.Review;
import org.icd4.commerce.domain.review.ReviewCreateRequest;

public interface ReviewRegister {

    Review create(ReviewCreateRequest request);

}
