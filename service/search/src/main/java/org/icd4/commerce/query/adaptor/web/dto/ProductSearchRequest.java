package org.icd4.commerce.query.adaptor.web.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProductSearchRequest{

    String keyword;
    String categoryId;
    String brand;
    int minPrice;
    int maxPrice;
    Map<String, List<String>> options;
    String sortField;
    String sortOrder;

}
