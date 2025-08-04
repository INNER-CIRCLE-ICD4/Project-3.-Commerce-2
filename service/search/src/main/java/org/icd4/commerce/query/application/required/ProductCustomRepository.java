package org.icd4.commerce.query.application.required;


import org.icd4.commerce.command.domain.Product;

public interface ProductCustomRepository {
    String registerProduct(Product product);
}
