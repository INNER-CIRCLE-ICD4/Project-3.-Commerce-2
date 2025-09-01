package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.search.SearchClient;
import org.icd4.commerce.adapter.stock.StockClient;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.ProductVariantResponse;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.request.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Validated
public class ProductCommandService {

    private final ProductRegisterService productRegisterService;
    private final ProductModifierService productModifierService;

    private final StockClient stockClient;
    private final SearchClient searchClient;

    public ProductResponse create(ProductCreateRequest request) {
        Product product = productRegisterService.create(request);

        // TODO 재고 벌크 등록 필요
        product.getAllVariants().forEach(variant -> {
            stockClient.updateStock(variant.getSku(), variant.getStockQuantity());
        });
        searchClient.registerProduct(product);

        return ProductResponse.fromDomain(product);
    }

    public ProductResponse changeProductInfo(String productId, String sellerId, ProductInfoUpdateRequest request) {
        Product product = productRegisterService.updateInfo(productId, sellerId, request);
        searchClient.registerProduct(product);
        return ProductResponse.fromDomain(product);
    }

    public ProductVariantResponse changeProductVariantInfo(String productId, String sku, String sellerId, ProductVariantUpdateRequest request) {
        Product product = productRegisterService.updateVariant(productId, sellerId, sku, request);
        return ProductVariantResponse.fromDomain(product.findVariantBySku(sku));
    }

    public ProductResponse changeCategory(String productId, String sellerId, ProductCategoryUpdateRequest request) {
        return ProductResponse.fromDomain(
                productModifierService.changeCategory(productId, sellerId, request.categoryId())
        );
    }

    public ProductResponse changeProductPrice(String productId, String sellerId, ProductPriceUpdateRequest request) {
        return ProductResponse.fromDomain(productModifierService.changeProductPrice(productId, sellerId, request.price()));
    }

    public ProductResponse activate(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.activate(productId, sellerId));
    }

    public ProductResponse inactivate(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.inactivate(productId, sellerId));
    }

    public ProductResponse deleteProduct(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.deleteProduct(productId, sellerId));
    }
}
