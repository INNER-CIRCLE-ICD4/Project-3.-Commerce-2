/**
 * 주문 Aggregate
 * 
 * 주문 도메인 모델을 구성하는 엔티티, 값 객체, 이벤트들을 포함합니다.
 * 
 * 구성 요소:
 * - Order: 주문 엔티티 (Aggregate Root)
 *     - 주문 생성, 상태 관리
 *     - 주문 취소, 배송 정보 관리
 * - OrderItem: 주문 아이템 엔티티
 *     - 주문된 개별 상품 정보
 * - OrderStatus: 주문 상태 Enum
 *     - PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
 * - DeliveryInfo: 배송 정보 Value Object
 *     - 수령인, 배송지 주소, 연락처
 * 
 * 도메인 이벤트:
 * - OrderCreatedEvent: 주문 생성 시 발행
 * - OrderStatusChangedEvent: 주문 상태 변경 시 발행
 */
package org.icd4.commerce.domain.order;