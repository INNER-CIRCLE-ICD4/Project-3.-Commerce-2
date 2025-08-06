package org.icd4.commerce.command.adaptor;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.ProductCommandService;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/search")
@RestController
@RequiredArgsConstructor
public class SearchApi {
    private final ProductCommandService productCommandService;

    @PostMapping("/_index")
    public String registerProductIndex(@RequestBody ProductCreateRequest product) {
        return productCommandService.registerProductIndex(product);
    }

    @DeleteMapping("/_index/{productId}")
    public void deleteProductIndex(@PathVariable String productId) {
        productCommandService.deleteProductIndex(productId);
    }
}
