package org.icd4.commerce.domain.product.model;

public enum VariantStatus {
    ACTIVE,       // 활성 (판매 가능한 상태로 설정됨)
    INACTIVE,     // 비활성 (판매자가 임시 비활성화)
    OUT_OF_STOCK,  // 품절
    DISCONTINUED  // 단종 (더 이상 판매하지 않음)
    }
