package org.icd4.commerce.shared.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(indexName = "product_index")
@Setting(settingPath = "elasticsearch/product_index.json")
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String sellerId;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String name;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
            }
    )
    private String brand;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String description;

    @Field(type = FieldType.Keyword)
    private BigDecimal basePrice;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Date, format = DateFormat.strict_date_optional_time)
    private String createdAt;

    @Field(type = FieldType.Date, format = DateFormat.strict_date_optional_time)
    private String updatedAt;

    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;

    @Field(type = FieldType.Keyword)
    private Set<String> productAttributes;

    @CompletionField
    private Completion autoCompleteSuggestions;

    @Field(type = FieldType.Nested)
    private List<ProductVariant> variants;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariant {
        @Field(type = FieldType.Keyword)
        private String sku;

        @Field(type = FieldType.Long)
        private Long price;

        @Field(type = FieldType.Keyword)
        private String status;

        @Field(type = FieldType.Nested)
        private List<ProductOption> optionCombination;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOption {
        @Field(type = FieldType.Keyword)
        private String name;

        @Field(type = FieldType.Keyword)
        private String value;
    }
}