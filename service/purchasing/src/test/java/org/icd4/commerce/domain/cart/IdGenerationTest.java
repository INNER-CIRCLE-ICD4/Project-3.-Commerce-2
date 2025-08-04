package org.icd4.commerce.domain.cart;

import org.icd4.commerce.common.idgenerator.ULIDUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ID 생성 테스트.
 * 
 * <p>CartId와 CartItemId가 ULID 형식을 올바르게 사용하는지 검증합니다.</p>
 */
@DisplayName("ID 생성 테스트")
class IdGenerationTest {
    
    @Test
    @DisplayName("CartId.generate()는 ULID 형식의 ID를 생성해야 한다")
    void cartIdShouldGenerateULIDFormat() {
        // when
        CartId cartId = CartId.generate();
        String idValue = cartId.value();
        
        // then
        assertThat(idValue).hasSize(26); // ULID는 26자
        assertThat(idValue).doesNotContain("-"); // ULID는 대시를 포함하지 않음
        assertThat(ULIDUtils.isValid(idValue)).isTrue(); // 유효한 ULID 형식인지 검증
    }
    
    @Test
    @DisplayName("CartItemId.generate()는 ULID 형식의 ID를 생성해야 한다")
    void cartItemIdShouldGenerateULIDFormat() {
        // when
        CartItemId cartItemId = CartItemId.generate();
        String idValue = cartItemId.value();
        
        // then
        assertThat(idValue).hasSize(26); // ULID는 26자
        assertThat(idValue).doesNotContain("-"); // ULID는 대시를 포함하지 않음
        assertThat(ULIDUtils.isValid(idValue)).isTrue(); // 유효한 ULID 형식인지 검증
    }
    
    @Test
    @DisplayName("생성된 ULID는 시간순으로 정렬 가능해야 한다")
    void generatedULIDsShouldBeSortableByTime() throws InterruptedException {
        // given
        CartId firstId = CartId.generate();
        Thread.sleep(10); // 시간 차이를 만들기 위해 잠시 대기
        CartId secondId = CartId.generate();
        
        // when - 문자열로 비교 (ULID는 사전식 정렬이 시간순 정렬과 같음)
        int comparison = firstId.value().compareTo(secondId.value());
        
        // then
        assertThat(comparison).isLessThan(0); // firstId < secondId
    }
}