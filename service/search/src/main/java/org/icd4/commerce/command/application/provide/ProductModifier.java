package org.icd4.commerce.command.application.provide;

public interface ProductModifier {
    void modifyProductInfo();

    void modifyProductPrice();

    void modifyProductVariantStatus();
}
