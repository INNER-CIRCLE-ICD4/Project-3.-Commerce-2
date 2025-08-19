package org.icd4.commerce.query.application.required;

import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;

import java.io.IOException;
import java.util.List;

public interface ProductSearcher {
    /**
     * 검색 쿼리, 필터(예: 색상, 사이즈), 정렬 조건을 모두 포함하여 상품을 검색합니다.
     * @param keyword 검색 키워드
     * @param filters 필터링 조건 (예: {"productAttributes": ["color:white", "size:M"]})
     * @param sortField 정렬 기준 필드 (예: "variants.price")
     * @param sortOrder 정렬 순서 (예: "asc" 또는 "desc")
     * @return 검색된 상품 목록
     * @throws IOException 통신 오류 발생 시
     */

    List<SearchResultResponse> searchWithAdvancedOptions(ProductSearchRequest criteria) throws IOException;

    /**
     * 검색어 접두어를 기반으로 자동완성 제안을 반환합니다.
     * @param prefix 사용자가 입력한 검색어의 접두어 (예: "샐러")
     * @return 자동완성 제안 목록
     * @throws IOException 통신 오류 발생 시
     */
    List<String> getAutocompleteSuggestions(String prefix) throws IOException;

}
