package org.icd4.commerce.query.application.required;

import org.icd4.commerce.shared.domain.Product;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// 삭제
public interface ProductSearcher {

    // 자주 쓰는 검색 기능
    List<Product> searchByKeyword(String keyword) throws IOException;
    List<Product> searchByCategory(String categoryId) throws IOException;


    // 모든 조합을 처리하는 유연한 메소드

    /**
     * 검색 쿼리, 필터(예: 색상, 사이즈), 정렬 조건을 모두 포함하여 상품을 검색합니다.
     * @param keyword 검색 키워드
     * @param filters 필터링 조건 (예: {"productAttributes": ["color:white", "size:M"]})
     * @param sortField 정렬 기준 필드 (예: "variants.price")
     * @param sortOrder 정렬 순서 (예: "asc" 또는 "desc")
     * @return 검색된 상품 목록
     * @throws IOException 통신 오류 발생 시
     */
    List<Product> searchWithAdvancedOptions(String keyword, Map<String, Object> filters, String sortField, String sortOrder) throws IOException;
    /**
     * 검색어 접두어를 기반으로 자동완성 제안을 반환합니다.
     * @param prefix 사용자가 입력한 검색어의 접두어 (예: "샐러")
     * @return 자동완성 제안 목록
     * @throws IOException 통신 오류 발생 시
     */
    List<String> getAutocompleteSuggestions(String prefix) throws IOException;

}
