package org.icd4.commerce.support;

import org.icd4.commerce.domain.cart.TimeProvider;

import java.time.LocalDateTime;

/**
 * 테스트용 TimeProvider 구현체.
 * 
 * <p>테스트에서 시간을 제어할 수 있도록 하는 구현체입니다.
 * 시간을 고정하거나 앞뒤로 이동시킬 수 있어 시간 관련 로직을 테스트할 때 유용합니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
public class TestTimeProvider implements TimeProvider {
    
    private LocalDateTime currentTime;
    private boolean autoAdvance = false;
    private long autoAdvanceMillis = 1;
    
    /**
     * 현재 시간으로 TestTimeProvider를 생성합니다.
     */
    public TestTimeProvider() {
        this.currentTime = LocalDateTime.now();
    }
    
    /**
     * 특정 시간으로 TestTimeProvider를 생성합니다.
     * 
     * @param fixedTime 고정할 시간
     */
    public TestTimeProvider(LocalDateTime fixedTime) {
        this.currentTime = fixedTime;
    }
    
    @Override
    public LocalDateTime now() {
        LocalDateTime result = currentTime;
        if (autoAdvance) {
            currentTime = currentTime.plusNanos(autoAdvanceMillis * 1_000_000);
        }
        return result;
    }
    
    /**
     * 현재 시간을 설정합니다.
     * 
     * @param newTime 새로운 시간
     */
    public void setNow(LocalDateTime newTime) {
        this.currentTime = newTime;
    }
    
    /**
     * 현재 시간을 지정된 일수만큼 앞으로 이동합니다.
     * 
     * @param days 이동할 일수
     */
    public void advanceDays(long days) {
        this.currentTime = currentTime.plusDays(days);
    }
    
    /**
     * 현재 시간을 지정된 시간만큼 앞으로 이동합니다.
     * 
     * @param hours 이동할 시간
     */
    public void advanceHours(long hours) {
        this.currentTime = currentTime.plusHours(hours);
    }
    
    /**
     * 현재 시간을 지정된 분만큼 앞으로 이동합니다.
     * 
     * @param minutes 이동할 분
     */
    public void advanceMinutes(long minutes) {
        this.currentTime = currentTime.plusMinutes(minutes);
    }
    
    /**
     * 현재 시간을 지정된 일수만큼 뒤로 이동합니다.
     * 
     * @param days 이동할 일수
     */
    public void rewindDays(long days) {
        this.currentTime = currentTime.minusDays(days);
    }
    
    /**
     * 현재 시간을 현재 시스템 시간으로 리셋합니다.
     */
    public void reset() {
        this.currentTime = LocalDateTime.now();
    }
    
    /**
     * 자동 시간 증가 기능을 활성화합니다.
     * now() 메서드가 호출될 때마다 지정된 밀리초만큼 시간이 자동으로 증가합니다.
     * 
     * @param millis 증가할 밀리초 (기본값: 1)
     */
    public void enableAutoAdvance(long millis) {
        this.autoAdvance = true;
        this.autoAdvanceMillis = millis;
    }
    
    /**
     * 자동 시간 증가 기능을 활성화합니다. (기본 1밀리초)
     */
    public void enableAutoAdvance() {
        enableAutoAdvance(1);
    }
    
    /**
     * 자동 시간 증가 기능을 비활성화합니다.
     */
    public void disableAutoAdvance() {
        this.autoAdvance = false;
    }
}