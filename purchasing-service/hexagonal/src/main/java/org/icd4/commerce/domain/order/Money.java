package org.icd4.commerce.domain.order;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * 금액과 통화를 함께 표현하는 값 객체입니다.
 *
 * <p>금액 관련 도메인 로직(합산, 비교 등)을 도메인 모델 내부로 옮기기 위해 사용되며,
 * 불변(immutable)한 Value Object로 설계되었습니다.</p>
 *
 * <p>주요 특징:
 * <ul>
 *   <li>금액(BigDecimal)과 통화(Currency)를 함께 보존</li>
 *   <li>불변성 보장 - setter 없음</li>
 *   <li>통화 일치 여부를 확인하는 도메인 규칙 포함</li>
 * </ul>
 * </p>
 */
@Getter
public class Money {
    private final BigDecimal amount;
    private final Currency currency;

    //기본 통화 상수(원화)
    private static final Currency KRW = Currency.getInstance("KRW");

    /**
     * Money 생성자
     *
     * @param amount   금액 (null 불가)
     * @param currency 통화 (null 불가)
     * @throws IllegalArgumentException null인 경우 예외 발생
     */
    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("금액과 통화는 null일 수 없습니다.");
        }
        this.amount = amount;
        this.currency = currency;
    }

    //정적 팩토리 메서드
    public static Money of(BigDecimal amount) {
        return new Money(amount, KRW);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    //ZERO 상수
    public static final Money ZERO = Money.of(BigDecimal.valueOf(0));

    @Override
    public int hashCode() {
        return 31 * amount.stripTrailingZeros().hashCode() + currency.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Money money = (Money) obj;
        return amount.compareTo(money.amount) == 0 &&
                currency.equals(money.currency);
    }

    /**
     * 금액을 더한 새 Money 객체 반환
     *
     * @param other 더할 Money 객체
     * @return 합산된 Money 객체
     * @throws IllegalArgumentException 통화가 다를 경우 예외 발생
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 금액이 0보다 큰지 확인
     *
     * @return true: 0보다 큼, false: 0 이하
     */
    public boolean isGreaterThanZero() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 통화 일치 여부를 검증합니다.
     *
     * @param other 비교 대상 Money
     * @throws IllegalArgumentException 통화가 다를 경우
     */
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("다른 통화 간 연산은 지원하지 않습니다.");
        }
    }

}