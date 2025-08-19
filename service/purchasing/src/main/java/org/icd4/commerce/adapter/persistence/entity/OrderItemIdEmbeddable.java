package org.icd4.commerce.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class OrderItemIdEmbeddable implements Serializable {

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "line_no", nullable = false)
    private int lineNo;       // 주문 내부 순번(1..n)

    public OrderItemIdEmbeddable(String orderId, int itemNo) {
        this.orderId = orderId;
        this.lineNo = itemNo;
    }
}