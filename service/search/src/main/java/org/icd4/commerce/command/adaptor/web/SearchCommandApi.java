package org.icd4.commerce.command.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.adaptor.elasticsearch.ElasticSearchProductDocumentIndexer;
import org.icd4.commerce.command.application.provided.ProductIndexingService;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

// 검색 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class SearchCommandApi {

    private final ProductIndexingService indexer;

    @PostMapping("/_index")
    public String searchProducts(@RequestBody Product request) throws IOException {
        return indexer.indexing(request);
    }

    @DeleteMapping("/_index/{productId}")
    public int getAutocompleteSuggestions(@PathVariable String productId)  {
        return indexer.delete(productId);
    }
}
