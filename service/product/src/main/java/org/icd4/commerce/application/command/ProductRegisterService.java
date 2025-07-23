package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.provided.ProductRegister;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.Product;
import org.icd4.commerce.domain.product.ProductCreateRequest;
import org.icd4.commerce.domain.product.ProductInfoUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductRegisterService implements ProductRegister {
    private final ProductFinder productFinder;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Product create(ProductCreateRequest request) {
        Product product = productRepository.save(Product.create(request));
        // TODO : Add event publishing logic here if needed
        // 재고 모듈로 재고 전달, 검색 모듈 혹은 읽기 모듈로 상품 정보 전달
        // 상품 등록과 재고 등록을 별도로 가져가고 싶음 협의 필요 :)

        return product;
    }

    @Override
    public Product updateInfo(String productId, ProductInfoUpdateRequest request) {
        Product product = productFinder.findById(productId);
        product.updateInfo(request);
        return productRepository.save(product);
    }
}
