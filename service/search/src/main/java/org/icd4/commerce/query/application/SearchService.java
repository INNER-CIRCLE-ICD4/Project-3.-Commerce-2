package org.icd4.commerce.query.application;
import org.icd4.commerce.query.application.dto.SearchResultDto;
import org.icd4.commerce.query.domain.ProductSearchRepository;

import java.util.List;
import java.util.stream.Collectors;

// 사용자로부터 검색을 요청 받는 곳
// dto로 변환해서 반환해줌
public class SearchService {
    private final ProductSearchRepository productSearchRepository;

    public SearchService(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    public List<SearchResultDto> search(String keyword) {
        return productSearchRepository.searchByKeyword(keyword)
                .stream()
                .map(product -> new SearchResultDto(product.getId(), product.getName(), product.getPrice()))
                .collect(Collectors.toList());
    }
}
