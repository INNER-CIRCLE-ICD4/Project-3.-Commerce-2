package org.icd4.commerce.common.idgenerator;


import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ULID를 자동으로 생성하는 어노테이션입니다.
 *
 * <p>사용 예시:
 * <pre>
 * {@code
 * @Entity
 * public class Product {
 *     @Id
 *     @GeneratedULID
 *     private String id;
 *
 *     // 다른 필드들...
 * }
 * }
 * </pre>
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(ULIDGenerator.class)  // Hibernate 6.x 최신 방식
public @interface GeneratedULID {
}

