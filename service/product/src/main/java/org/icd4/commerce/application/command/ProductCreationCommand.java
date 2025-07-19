package org.icd4.commerce.application.command;

import org.icd4.commerce.domain.product.ProductMoney;
import org.icd4.commerce.domain.product.ProductOption;

import java.util.List;

public record ProductCreationCommand (
        String sellerId,
        String name,
        String brand,
        String description,
        String categoryId,
        ProductMoney price,
        List<ProductOption> options
) {}
