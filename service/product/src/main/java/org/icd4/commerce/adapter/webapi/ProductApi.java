package org.icd4.commerce.adapter.webapi;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.ProductVariantResponse;
import org.icd4.commerce.application.command.ProductCommandService;
import org.icd4.commerce.application.query.ProductQueryService;
import org.icd4.commerce.domain.product.request.ProductCategoryUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductApi {
    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findById(@PathVariable String productId) {
        return ResponseEntity.ok(productQueryService.findById(productId));
    }

    @GetMapping("/{productId}/variant")
    public ResponseEntity<List<ProductVariantResponse>> findAllVariants(@PathVariable String productId) {
        List<ProductVariantResponse> variants = productQueryService.findAllVariants(productId);
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/{productId}/{sku}")
    public ResponseEntity<ProductVariantResponse> findVariantBySku(
            @PathVariable String productId, @PathVariable String sku) {
        return ResponseEntity.ok(productQueryService.findVariantByProductIdAndSku(productId, sku));
    }

    @GetMapping("/variant/{sku}")
    public ResponseEntity<ProductVariantResponse> findVariantBySku(
            @PathVariable String sku) {
        return ResponseEntity.ok(productQueryService.findVariantBySku(sku));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productCommandService.create(request));
    }

    @PatchMapping("/{productId}/category")
    public ResponseEntity<ProductResponse> changeCategory(
            @PathVariable String productId,
            @RequestHeader("X-Seller-Id") String sellerId,
            @Valid @RequestBody ProductCategoryUpdateRequest request) {
        return ResponseEntity.ok(productCommandService.changeCategory(productId, sellerId, request));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> changeProductInfo(@PathVariable String productId,
                                                             @Valid @RequestBody ProductInfoUpdateRequest request) {
        return ResponseEntity.ok(productCommandService.changeProductInfo(productId, request.sellerId(), request));
    }

    @PatchMapping("/{productId}/{sku}")
    public ResponseEntity<ProductVariantResponse> changeProductVariantInfo(@PathVariable String productId,
                                                                           @PathVariable String sku,
                                                                           @Valid @RequestBody ProductVariantUpdateRequest request) {
        return ResponseEntity.ok(productCommandService.changeProductVariantInfo(productId, sku, request.sellerId(), request));
    }


    @PatchMapping("{productId}/price")
    public ResponseEntity<ProductResponse> changePrice(
            @PathVariable String productId,
            @RequestHeader("X-Seller-Id") String sellerId,
            @Valid @RequestBody ProductPriceUpdateRequest request) {
        return ResponseEntity.ok(productCommandService.changeProductPrice(productId, sellerId, request));
    }

    @PatchMapping("{productId}/activate")
    public ResponseEntity<ProductResponse> activate(
            @PathVariable String productId,
            @RequestHeader("X-Seller-Id") String sellerId) {
        return ResponseEntity.ok(productCommandService.activate(productId, sellerId));
    }

    @PatchMapping("{productId}/inactivate")
    public ResponseEntity<ProductResponse> inactivate(
            @PathVariable String productId,
            @RequestHeader("X-Seller-Id") String sellerId) {
        return ResponseEntity.ok(productCommandService.inactivate(productId, sellerId));
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<ProductResponse> delete(
            @PathVariable String productId,
            @RequestHeader("X-Seller-Id") String sellerId) {
        return ResponseEntity.ok(productCommandService.deleteProduct(productId, sellerId));
    }

}
