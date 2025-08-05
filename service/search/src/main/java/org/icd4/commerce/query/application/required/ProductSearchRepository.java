package org.icd4.commerce.query.application.required;



import org.icd4.commerce.shared.domain.Product;

import java.util.List;

public interface ProductSearchRepository {
    List<Product> searchByKeyword(String keyword);
}
