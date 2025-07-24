package org.icd4.commerce.adapter.webapi;


import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.application.command.ProductCommandService;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductApi {
    private final ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productCommandService.create(request));
    }

}
