package org.icd4.commerce.command.domain;

// 검색엔진에 데이터 저장/삭제.. 하는 메서드 정의
public interface ProductSyncRepository {
    void save(ProductSync productSync);
}
