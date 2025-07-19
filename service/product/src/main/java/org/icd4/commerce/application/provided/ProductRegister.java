package org.icd4.commerce.application.provided;

import org.icd4.commerce.application.command.ProductCreationCommand;
import org.icd4.commerce.domain.product.Product;

public interface ProductRegister {

    Product createProduct(ProductCreationCommand command);
}
