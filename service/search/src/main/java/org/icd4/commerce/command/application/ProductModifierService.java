package org.icd4.commerce.command.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.provide.ProductModifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductModifierService implements ProductModifier {
    @Override
    public void modifyProductInfo() {

    }

    @Override
    public void modifyProductPrice() {

    }

    @Override
    public void modifyProductVariantStatus() {

    }
}
