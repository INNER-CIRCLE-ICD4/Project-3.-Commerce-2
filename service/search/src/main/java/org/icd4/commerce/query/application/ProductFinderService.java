package org.icd4.commerce.query.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.provide.ProductFinder;
import org.icd4.commerce.query.application.required.ProductCustomRepository;
import org.icd4.commerce.query.application.required.ProductSearchRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFinderService implements ProductFinder {
    private final ProductSearchRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
}
