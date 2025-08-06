package org.icd4.commerce.query.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultDto;
import org.icd4.commerce.query.application.required.ProductSearchElasticRepository;

import java.util.List;
import java.util.stream.Collectors;

// 사용자로부터 검색을 요청 받는 곳
// dto로 변환해서 반환해줌
@RequiredArgsConstructor
public class SearchService {
    private final ProductSearchElasticRepository productSearchRepository;

    public List<SearchResultDto> search(String keyword) {
        return productSearchRepository.findAllByNameAndBrandAndDescriptionAndCategoryIdMatches(keyword).stream()
                .map(product -> SearchResultDto.of(
                        product.getId(),
                        product.getSellerId(),
                        product.getName(),
                        product.getBrand(),
                        product.getBasePrice()
                ))
                .collect(Collectors.toList());
    }
}
