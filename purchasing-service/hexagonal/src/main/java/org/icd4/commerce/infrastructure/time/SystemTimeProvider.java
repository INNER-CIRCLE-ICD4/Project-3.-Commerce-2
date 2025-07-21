package org.icd4.commerce.infrastructure.time;

import org.icd4.commerce.domain.cart.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 시스템 시간을 제공하는 TimeProvider 구현체.
 */
@Component
public class SystemTimeProvider implements TimeProvider {
    
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}