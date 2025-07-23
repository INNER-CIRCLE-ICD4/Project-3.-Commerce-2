package org.icd4.commerce.common.idgenerator;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

import java.time.Instant;

public final class ULIDUtils {
    private ULIDUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 새로운 ULID를 생성합니다.
     */
    public static String generate() {
        return UlidCreator.getUlid().toString();
    }

    /**
     * 특정 시점의 ULID를 생성합니다.
     */
    public static String generateAt(Instant timestamp) {
        return UlidCreator.getUlid(timestamp.toEpochMilli()).toString();
    }

    /**
     * ULID에서 타임스탬프를 추출합니다.
     */
    public static Instant extractTimestamp(String ulid) {
        return Instant.ofEpochMilli(Ulid.from(ulid).getTime());
    }

    /**
     * ULID 형식이 유효한지 검증합니다.
     */
    public static boolean isValid(String ulid) {
        try {
            Ulid.from(ulid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * ULID를 바이너리 형태로 변환합니다 (데이터베이스 저장용).
     */
    public static byte[] toBytes(String ulid) {
        return Ulid.from(ulid).toBytes();
    }

    /**
     * 바이너리에서 ULID 문자열로 변환합니다.
     */
    public static String fromBytes(byte[] bytes) {
        return Ulid.from(bytes).toString();
    }

}
