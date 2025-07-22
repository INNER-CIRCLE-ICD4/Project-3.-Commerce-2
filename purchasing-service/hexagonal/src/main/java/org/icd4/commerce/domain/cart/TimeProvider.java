package org.icd4.commerce.domain.cart;

import java.time.LocalDateTime;

/**
 * 시간 제공자 인터페이스 - 도메인 레이어의 시간 추상화 포트.
 * 
 * <p>헥사고날 아키텍처에서 도메인이 시스템 시간이라는 외부 인프라에 직접 의존하지 않도록
 * 추상화한 포트입니다. 이를 통해 도메인 레이어는 순수하게 유지되며,
 * 다양한 시간 정책을 유연하게 적용할 수 있습니다.</p>
 * 
 * <p>설계 원칙:</p>
 * <ul>
 *   <li><b>의존성 역전</b>: 도메인이 구체적인 시스템 시간이 아닌 추상화에 의존</li>
 *   <li><b>개방-폐쇄 원칙</b>: 새로운 시간 정책 추가 시 도메인 코드 수정 불필요</li>
 *   <li><b>테스트 가능성</b>: 시간을 제어하여 확정적인 테스트 가능</li>
 * </ul>
 * 
 * <p>구현 예시:</p>
 * <ul>
 *   <li><b>SystemTimeProvider</b>: 실제 시스템 시간 사용 (프로덕션)</li>
 *   <li><b>FixedTimeProvider</b>: 고정된 시간 반환 (테스트)</li>
 *   <li><b>UserTimeZoneProvider</b>: 사용자별 시간대 적용 (글로벌 서비스)</li>
 *   <li><b>NtpTimeProvider</b>: NTP 서버 동기화 시간 (분산 시스템)</li>
 *   <li><b>AuditTimeProvider</b>: 특정 시점 기준 조회 (감사/규정 준수)</li>
 * </ul>
 * 
 * @author Jooeun
 * @since 1.0
 * @see Cart#isExpired()
 * @see Cart#Cart(CartId, CustomerId, TimeProvider)
 */
public interface TimeProvider {
    /**
     * 현재 시간을 반환합니다.
     * 
     * @return 현재 시간
     */
    LocalDateTime now();
    
    /**
     * 시스템 시간을 사용하는 기본 구현체.
     * 
     * <p>프로덕션 환경에서 사용되는 구현체로,
     * 실제 시스템 시간을 반환합니다.</p>
     */
    class SystemTimeProvider implements TimeProvider {
        /**
         * 시스템의 현재 시간을 반환합니다.
         * 
         * @return 시스템의 현재 시간
         */
        @Override
        public LocalDateTime now() {
            return LocalDateTime.now();
        }
    }
}