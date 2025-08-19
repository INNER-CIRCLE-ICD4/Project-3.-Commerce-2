package org.icd4.commerce.command.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.provided.ProductIndexingService;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

// 검색 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class SearchCommandApi {

    private final ProductIndexingService indexer;

    @PostMapping
    public String create(@RequestBody ProductCreateRequest request) throws IOException {
        return indexer.indexing(request);
    }

    @DeleteMapping("/{productId}")
    public void delete(@PathVariable String productId) {
        indexer.delete(productId);
    }
}
