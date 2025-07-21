package org.icd4.commerce.config;

import org.icd4.commerce.domain.cart.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TimeProvider 빈 설정 클래스.
 * 
 * <p>환경별로 다른 TimeProvider 구현체를 주입합니다:</p>
 * <ul>
 *   <li>프로덕션/개발: SystemTimeProvider (실제 시스템 시간)</li>
 *   <li>테스트: TestTimeProvider (제어 가능한 시간)</li>
 * </ul>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Configuration
public class TimeProviderConfig {
    
    /**
     * 프로덕션 및 개발 환경용 TimeProvider.
     * 실제 시스템 시간을 반환합니다.
     * 
     * @return SystemTimeProvider 인스턴스
     */
    @Bean
    @Profile({"prod", "dev", "local", "default"})
    public TimeProvider systemTimeProvider() {
        return new TimeProvider.SystemTimeProvider();
    }
    
    /**
     * 테스트 환경 전용 TimeProvider.
     * 시간을 제어할 수 있는 테스트용 구현체입니다.
     * 
     * @return TestTimeProvider 인스턴스
     */
    @Bean
    @Profile("test")
    public TimeProvider testTimeProvider() {
        // 테스트 환경에서는 TestTimeProvider 구현체 사용
        // TestTimeProvider는 테스트 패키지에 별도 구현
        return new TimeProvider.SystemTimeProvider(); // 임시로 SystemTimeProvider 반환
    }
}