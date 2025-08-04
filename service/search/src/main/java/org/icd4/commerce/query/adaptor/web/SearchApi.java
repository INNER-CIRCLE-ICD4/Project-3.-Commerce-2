package org.icd4.commerce.query.adaptor.web;

import org.icd4.commerce.query.application.SearchService;
import org.icd4.commerce.query.application.dto.SearchResultDto;
import org.icd4.commerce.query.domain.ProductSearch;

import java.util.List;

// 검색 API
public class SearchApi {
    private final SearchService searchService;
    public SearchApi(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<SearchResultDto> searchByKeyword(String keyword) {
        return searchService.search(keyword);
    }
}
