/**
 * 도메인 모델 계층
 * 
 * 비즈니스 로직과 도메인 규칙을 포함하는 핵심 계층입니다.
 * 
 * Aggregate 구조:
 * - cart: 장바구니 Aggregate
 *     - Cart (Aggregate Root)
 *     - CartItem
 * - order: 주문 Aggregate
 *     - Order (Aggregate Root)
 *     - OrderItem
 *     - Value Objects
 *     - Domain Events
 */
package org.icd4.commerce.domain;