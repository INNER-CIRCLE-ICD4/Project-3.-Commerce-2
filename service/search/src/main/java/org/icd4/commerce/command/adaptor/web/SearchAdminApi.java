package org.icd4.commerce.command.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.IndexManager;
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
    private final IndexManager indexManager;

    @PostMapping
    public ResponseEntity<String> createProductIndex() throws IOException {
        indexManager.createIndex();
        return ResponseEntity.ok("Product index creation initiated.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteProductIndex() throws IOException {
        indexManager.deleteIndex();
        return ResponseEntity.ok("Product index deletion initiated.");
    }
}
