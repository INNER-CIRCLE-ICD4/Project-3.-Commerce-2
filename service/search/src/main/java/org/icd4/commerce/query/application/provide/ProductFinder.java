package org.icd4.commerce.query.application.provide;

public interface ProductFinder {
    // 검색 관련
    /**
     * // 단순 검색
     * List<Product> search(String keyword);
     * List<Product> searchByCategory(String category);
     *
     * // 단순 필터링
     * List<Product> filterByPriceRange(BigDecimal min, BigDecimal max);
     * List<Product> filterByBrand(String brand);
     *
     * // 단순 정렬
     * List<Product> findAllSortedByPrice(SortDirection direction);
     * List<Product> findAllSortedByName(SortDirection direction);
     *
     * // 검색 + 필터링
     * List<Product> searchAndFilter(String keyword, ProductFilter filter);
     * List<Product> searchByCategoryAndFilter(String category, ProductFilter filter);
     *
     * // 검색 + 정렬
     * List<Product> searchAndSort(String keyword, ProductSort sort);
     *
     * // 필터링 + 정렬
     * List<Product> filterAndSort(ProductFilter filter, ProductSort sort);
     *
     * // 검색 + 필터링 + 정렬 (전체 조합)
     * List<Product> searchFilterAndSort(String keyword, ProductFilter filter, ProductSort sort);
     */
}
