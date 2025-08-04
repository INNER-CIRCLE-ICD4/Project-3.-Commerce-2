package org.icd4.commerce.domain.order;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {
    public PaymentId {
        Objects.requireNonNull(value, "PaymentId는 null일 수 없습니다.");
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId from(String value) {
        return new PaymentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
