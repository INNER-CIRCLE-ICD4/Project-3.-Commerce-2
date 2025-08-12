package org.icd4.commerce.query.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.dto.SearchResultDto;
import org.icd4.commerce.query.application.required.ProductSearcher;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 사용자로부터 검색을 요청 받는 곳
// dto로 변환해서 반환해줌
@Service
@RequiredArgsConstructor
public class SearchService {
    private final ProductSearcher productSearcher;

    public List<SearchResultDto> search(String keyword, String categoryId, Map<String, Object> filters, String sortField, String sortOrder) throws IOException {
        List<Product> products;

        // filters, sortField, sortOrder가 없는 경우를 단순 검색으로 간주
        boolean isSimpleSearch = (filters == null || filters.isEmpty()) && sortField == null && sortOrder == null;

        if (keyword != null && !keyword.isBlank() && categoryId == null && isSimpleSearch) {
            // 1. 키워드만 있는 경우
            products = productSearcher.searchByKeyword(keyword);
        } else if (categoryId != null && !categoryId.isBlank() && keyword == null && isSimpleSearch) {
            // 2. 카테고리만 있는 경우
            products = productSearcher.searchByCategory(categoryId);
        } else {
            // 3. 그 외 모든 복잡한 케이스 (키워드+필터, 키워드+카테고리, 정렬 등)
            products = productSearcher.searchWithAdvancedOptions(keyword, filters, sortField, sortOrder);
        }

        return products.stream()
                .map(product -> SearchResultDto.of(
                        product.getId(),
                        product.getSellerId(),
                        product.getName(),
                        product.getBrand(),
                        product.getBasePrice()
                ))
                .collect(Collectors.toList());
    }

    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        return productSearcher.getAutocompleteSuggestions(prefix);
    }


    /*
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
    */

}
