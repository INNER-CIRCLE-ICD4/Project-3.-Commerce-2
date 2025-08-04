package org.icd4.commerce.query.domain;


import java.util.List;

public interface ProductSearchRepository {
    List<ProductSearch> searchByKeyword(String keyword);
}
