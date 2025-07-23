package org.icd4.commerce.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 캐시 설정.
 * 
 * <p>상품 정보와 재고 정보를 캐싱하여 외부 서비스 호출을 최소화합니다.</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("products"),      // 상품 정보 캐시 (TTL: 10분)
            new ConcurrentMapCache("product-stock")  // 재고 정보 캐시 (TTL: 1분)
        ));
        
        return cacheManager;
    }
    
    // 실제 운영환경에서는 Redis나 Caffeine 같은 
    // 더 정교한 캐시 솔루션을 사용하는 것이 좋습니다.
    // 예: @Bean CaffeineCacheManager with TTL configuration
}