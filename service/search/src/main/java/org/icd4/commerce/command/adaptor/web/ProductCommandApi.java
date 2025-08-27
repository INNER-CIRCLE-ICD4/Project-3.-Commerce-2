package org.icd4.commerce.command.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.provided.ProductCommandService;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

// 검색 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductCommandApi {
    private final ProductCommandService productCommandService;

    @PostMapping
    public String create(@RequestBody ProductCreateRequest request) throws IOException {
        return productCommandService.create(request);
    }

    @DeleteMapping("/{productId}")
    public void delete(@PathVariable String productId) {
        productCommandService.delete(productId);
    }

    @PatchMapping("/{productId}/price")
    public String updatePrice(@PathVariable String productId, @RequestParam int price) throws IOException {
        return productCommandService.updatePrice(productId, price);
    }

    @PatchMapping("/{productId}/stock")
    public String updateStock(@PathVariable String productId,
                            @RequestParam String sku,
                            @RequestParam int stock) throws IOException {
        return productCommandService.updateStock(productId, sku, stock);
    }

    @PatchMapping("/{productId}/variant_status")
    public String updateVariantStatus(@PathVariable String productId,
                                    @RequestParam String sku,
                                    @RequestParam String variantStatus) throws IOException {
        return productCommandService.updateVariantStatus(productId, sku, variantStatus);
    }
}
