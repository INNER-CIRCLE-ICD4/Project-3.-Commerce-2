package org.icd4.commerce.command.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.ProductCommandElasticRepository;
import org.icd4.commerce.command.application.required.ProductCommandRepository;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductDocumentRegisterService implements ProductDocumentRegister {
    private final ProductCommandRepository productCommandRepository;
    private final ProductCommandElasticRepository productCommandElasticRepository;

    @Override
    public String create(ProductCreateRequest request) throws IOException {
        return productCommandRepository.createProductDocument(request);
    }

    @Override
    public void delete(String productId) {
        productCommandElasticRepository.deleteById(productId);
    }
}
