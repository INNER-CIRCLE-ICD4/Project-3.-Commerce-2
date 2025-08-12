package org.icd4.commerce.command.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.provide.ProductRegister;
import org.icd4.commerce.command.application.required.ProductCommandCustomRepository;
import org.icd4.commerce.command.application.required.ProductCommandRepository;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductRegisterService implements ProductRegister {
    private final ProductCommandRepository productCommandRepository;
    private final ProductCommandCustomRepository productCustomRepository;

    @Override
    @Transactional
    public String registerProductIndex(Product product) {
        return productCustomRepository.registerProduct(product);
    }

    @Override
    @Transactional
    public void deleteProductIndex(String productId) {
        productCommandRepository.deleteProductById(productId);
    }
}
