package org.icd4.commerce.query.application.required;

import org.icd4.commerce.shared.domain.Product;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
*
* 현재 query 인터페이스가 많다고 느껴져서 고민 하다가
* "인터페이스는 어떤 검색엔진을 쓰는지 전혀 모른다" 의 관점으로
* ProductSearcher 하나로 통합해보면 어떨까 싶어 만들었습니다
*
* Spring Data 를 사용하면 메소드 이름만으로 빠르게 개발할 수 있는 장점이 있지만,
* 클린 아키텍처 원칙에 따른 하이브리드 방식으로 접근해보는 것도 좋을 것 같다는 생각이 들어 작성해보았습니다.
* (의존성 역전 원칙: 코드 의존성이 추상에 의존하며 구체에는 의존하지 않는 시스템
* (소프트웨어를 여러 계층으로 나누어, 소스 코드의 의존성이 안쪽으로만 향하도록 설계하고자 함)
*
*
* 검토해보시고, 괜찮은 방향이라고 생각되시면 나머지는 삭제하면 어떨지 싶습니다!
*
* */
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
