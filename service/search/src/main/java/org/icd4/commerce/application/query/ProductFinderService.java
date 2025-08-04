package org.icd4.commerce.application.query;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.required.ProductCustomRepository;
import org.icd4.commerce.application.required.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFinderService implements ProductFinder {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
}
