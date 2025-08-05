package org.icd4.commerce.command.application.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

   /* @KafkaListener(topics = {
            PRODUCT_CREATE,
            PRODUCT_UPDATE,
            PRODUCT_DELETE
    })
    public void listen(String message, Acknowledgment ack) {
        Event event = Event.fromJson(message);
        if (event != null) {
            handleEvent();
        }
    }
    ack.acknowledge();*/
    // 카프카 토픽 Consume
    public void listen() {
        // consume 한 이벤트 타입에 맞게 이벤트 실행
        // Product indexing/ Product re-indexing / Product delete
        // 카프카에 ack 날려주기
    }
}
