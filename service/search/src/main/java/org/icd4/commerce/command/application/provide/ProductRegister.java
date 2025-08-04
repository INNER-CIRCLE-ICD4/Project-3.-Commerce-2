package org.icd4.commerce.command.application.provide;

import org.icd4.commerce.shared.domain.Product;

public interface ProductRegister {
    String registerProductIndex(Product product);

    void deleteProductIndex(String productId);
}
