package org.icd4.commerce.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;

/**
 * 모든 도메인 이벤트의 기본 인터페이스
 * Kafka 메시지로 전송될 때 타입 정보를 포함하여 역직렬화 가능
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
public interface DomainEvent {
    /**
     * 이벤트가 발생한 시간
     */
    LocalDateTime getOccurredAt();
    
    /**
     * 이벤트를 발생시킨 Aggregate의 ID
     */
    String getAggregateId();
    
    /**
     * 이벤트 타입 (예: OrderCreated, CartItemAdded)
     */
    String getEventType();
}