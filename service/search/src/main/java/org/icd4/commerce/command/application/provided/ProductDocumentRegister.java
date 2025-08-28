package org.icd4.commerce.command.application.provided;

import org.icd4.commerce.shared.domain.ProductCreateRequest;

import java.io.IOException;

public interface ProductDocumentRegister {

    String create(ProductCreateRequest request) throws IOException;

    void delete(String productId);
}
