package org.icd4.commerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 관련 설정을 담당하는 구성 클래스.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>JPA Repository 자동 스캔 및 활성화</li>
 *   <li>JPA Auditing 기능 활성화 (생성/수정 시간 자동 관리)</li>
 *   <li>트랜잭션 관리 활성화</li>
 * </ul>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.icd4.commerce.adapter.persistence")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    
    /**
     * JPA 설정은 대부분 Spring Boot의 자동 설정을 활용합니다.
     * 필요시 추가적인 빈 정의를 여기에 추가할 수 있습니다.
     */
}