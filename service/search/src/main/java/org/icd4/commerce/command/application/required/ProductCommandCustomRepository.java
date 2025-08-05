package org.icd4.commerce.command.application.required;

import org.icd4.commerce.shared.domain.Product;

public interface ProductCommandCustomRepository {
    String registerProduct(Product product);
}
