package org.icd4.commerce.config;

import org.icd4.commerce.domain.cart.TimeProvider;
import org.icd4.commerce.support.TestTimeProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * 테스트 전용 설정 클래스.
 * 
 * <p>테스트 환경에서만 활성화되는 빈들을 정의합니다.
 * @TestConfiguration을 사용하여 메인 설정을 오버라이드합니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@TestConfiguration
@Profile("test")
public class TestConfig {
    
    /**
     * 테스트용 TimeProvider 빈.
     * 
     * <p>@Primary를 사용하여 기본 TimeProvider보다 우선순위를 높입니다.
     * 이를 통해 테스트에서 시간을 제어할 수 있습니다.</p>
     * 
     * @return TestTimeProvider 인스턴스
     */
    @Bean
    @Primary
    public TimeProvider testTimeProvider() {
        return new TestTimeProvider();
    }
}