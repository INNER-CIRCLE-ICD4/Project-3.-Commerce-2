package org.icd4.commerce.query.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.ProductIndexManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/index")
public class SearchAdminApi {
    private final ProductIndexManager productIndexManager;

    @PostMapping("/create/product")
    public ResponseEntity<String> createProductIndex() throws IOException {
        productIndexManager.createIndex();
        return ResponseEntity.ok("Product index creation initiated.");
    }

    @DeleteMapping("/delete/product")
    public ResponseEntity<String> deleteProductIndex() throws IOException {
        productIndexManager.deleteIndex();
        return ResponseEntity.ok("Product index deletion initiated.");
    }
}
