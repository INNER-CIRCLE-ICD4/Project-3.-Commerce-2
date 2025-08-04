package org.icd4.commerce.adapter.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.icd4.commerce.adapter.persistence.entity.CartItemJpaEntity;
import org.icd4.commerce.adapter.persistence.entity.CartJpaEntity;
import org.icd4.commerce.domain.cart.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼.
 * 
 * <p>순수 도메인 모델과 JPA 엔티티 간의 양방향 변환을 처리합니다.
 * 이를 통해 도메인 레이어는 JPA에 대한 의존성 없이 순수하게 유지됩니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Component
public class CartEntityMapper {
    
    private final ObjectMapper objectMapper;
    private final TimeProvider timeProvider;
    
    /**
     * CartEntityMapper 생성자.
     * 
     * @param objectMapper JSON 직렬화/역직렬화를 위한 ObjectMapper
     * @param timeProvider 시간 제공자
     */
    public CartEntityMapper(ObjectMapper objectMapper, TimeProvider timeProvider) {
        this.objectMapper = objectMapper;
        this.timeProvider = timeProvider;
    }
    
    /**
     * 도메인 Cart를 JPA 엔티티로 변환합니다.
     * 
     * @param cart 도메인 Cart
     * @return CartJpaEntity
     */
    public CartJpaEntity toEntity(Cart cart) {
        CartJpaEntity entity = new CartJpaEntity(
            cart.getId().value(),
            cart.getCustomerId().value(),
            cart.getCreatedAt(),
            cart.getLastModifiedAt(),
            cart.isConverted()
        );
        
        // 장바구니 아이템들 변환 및 연관관계 설정
        List<CartItemJpaEntity> itemEntities = cart.getItems().stream()
            .map(item -> toItemEntity(item, entity))
            .collect(Collectors.toList());
        
        entity.setItems(itemEntities);
        
        return entity;
    }
    
    /**
     * JPA 엔티티를 도메인 Cart로 변환합니다.
     * 
     * @param entity CartJpaEntity
     * @return 도메인 Cart
     */
    public Cart toDomain(CartJpaEntity entity) {
        List<CartItem> items = entity.getItems().stream()
            .map(this::toItemDomain)
            .collect(Collectors.toList());
        
        return new Cart(
            new CartId(entity.getId()),
            new CustomerId(entity.getCustomerId()),
            items,
            entity.getCreatedAt(),
            entity.getLastModifiedAt(),
            entity.isConverted(),
            timeProvider
        );
    }
    
    /**
     * 도메인 CartItem을 JPA 엔티티로 변환합니다.
     * 
     * @param item 도메인 CartItem
     * @param cart 부모 CartJpaEntity
     * @return CartItemJpaEntity
     */
    private CartItemJpaEntity toItemEntity(CartItem item, CartJpaEntity cart) {
        String optionsJson = serializeOptions(item.getOptions());
        
        CartItemJpaEntity entity = new CartItemJpaEntity(
            item.getId().value(),
            item.getProductId().value().toString(),
            optionsJson,
            item.getQuantity(),
            item.getAddedAt(),
            item.getLastModifiedAt(),
            item.isAvailable(),
            item.getUnavailableReason()
        );
        
        entity.setCart(cart);
        
        return entity;
    }
    
    /**
     * JPA 엔티티를 도메인 CartItem으로 변환합니다.
     * 
     * @param entity CartItemJpaEntity
     * @return 도메인 CartItem
     */
    private CartItem toItemDomain(CartItemJpaEntity entity) {
        ProductOptions options = deserializeOptions(entity.getOptions());
        
        return new CartItem(
            new CartItemId(entity.getId()),
            new ProductId(entity.getProductId()),
            options,
            entity.getQuantity(),
            entity.getAddedAt(),
            entity.getLastModifiedAt(),
            entity.isAvailable(),
            entity.getUnavailableReason(),
            timeProvider
        );
    }
    
    /**
     * ProductOptions를 JSON 문자열로 직렬화합니다.
     * 
     * @param options ProductOptions
     * @return JSON 문자열
     */
    private String serializeOptions(ProductOptions options) {
        try {
            return objectMapper.writeValueAsString(options.getOptions());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ProductOptions", e);
        }
    }
    
    /**
     * JSON 문자열을 ProductOptions로 역직렬화합니다.
     * 
     * @param json JSON 문자열
     * @return ProductOptions
     */
    private ProductOptions deserializeOptions(String json) {
        if (json == null || json.isEmpty()) {
            return new ProductOptions(new HashMap<>());
        }
        
        try {
            Map<String, String> options = objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class));
            return new ProductOptions(options);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize ProductOptions", e);
        }
    }
}