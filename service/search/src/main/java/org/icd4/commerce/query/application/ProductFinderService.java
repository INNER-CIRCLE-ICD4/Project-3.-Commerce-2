package org.icd4.commerce.query.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.provide.ProductFinder;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.query.application.required.ProductCustomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFinderService implements ProductFinder {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
}
