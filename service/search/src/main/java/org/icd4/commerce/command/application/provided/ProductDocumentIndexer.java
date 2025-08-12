package org.icd4.commerce.command.application.provided;

import org.icd4.commerce.shared.domain.Product;

import java.io.IOException;

//
/*
* 엘라스틱서치 인덱스에 상품 문서를 동기화 하는 역할
*
* 1. ProductModifier 과 같이 부분 업데이트 요청을 보내면
* 변경된 필드만 업데이트하므로 인덱싱 부하가 적은 장점이 있지만,
*  - 이벤트 리스너가 어떤 필드가 변경되었는지 분석하는 로직을 추가해줘야함
*  - 새로운 필드가 추가되면 관련 업데이트 메소드를 추가해줘야함.
*
* 2. 이벤트 리스너에서 받은 Product 객체를 통으로 인덱싱하는 방법 (indexProduct)
* 이렇게 하면 어떤 필드가 바뀌었는지 신경 쓸 필요 없으므로 "데이터 불일치" 를 방지할 수 있음
* 데이터 필드 1개만 바뀌었는데도, 상품 문서 하나를 새로운 최신 문서로 통째로 교체해야하는 점은
* 성능이 부담될 수 있는 단점이 있긴함. (네트워크 오버헤드)
* 하지만 상품 문서 하나의 크기가 수십, 수백 MB가 아니라면 큰 문제가 아닐 것으로 생각..
*
*
* 2번대로 하는 것은 어떨지 논의 필요! (-> 나머지 인터페이스 삭제 필요)
*
*
* */
public interface ProductDocumentIndexer {
    void indexProduct(Product product) throws IOException;
    void deleteProduct(String productId) throws IOException;
}
