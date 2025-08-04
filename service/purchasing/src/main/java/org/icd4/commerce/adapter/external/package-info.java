/**
 * Outbound Adapter - 외부 서비스 연동 계층
 * 
 * 다른 마이크로서비스와의 통신을 담당하는 어댑터들이 위치합니다.
 * 
 * 구현체:
 * - ProductServiceAdapter: 상품 서비스와의 통신
 *     - REST API 또는 gRPC를 통한 상품 정보 조회
 *     - 재고 확인 및 차감 요청
 * - PaymentServiceAdapter: 결제 서비스와의 통신
 *     - 결제 요청 및 취소
 *     - 결제 상태 확인
 */
package org.icd4.commerce.adapter.external;