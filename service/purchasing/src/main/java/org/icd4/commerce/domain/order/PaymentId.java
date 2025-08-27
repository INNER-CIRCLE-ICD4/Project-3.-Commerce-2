package org.icd4.commerce.domain.order;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(String value) {
    public PaymentId {
        Objects.requireNonNull(value, "PaymentId는 null일 수 없습니다.");
    }

    public static PaymentId from(String value) {
        return new PaymentId(value);
    }

}
