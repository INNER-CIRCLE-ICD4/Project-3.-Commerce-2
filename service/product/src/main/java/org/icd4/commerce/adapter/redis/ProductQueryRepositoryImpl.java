package org.icd4.commerce.adapter.redis;

import commerce.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.application.required.ProductQueryRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "product::product::%s";

    @Override
    public void create(ProductResponse response, Duration ttl) {
        redisTemplate.opsForValue().set(generateKey(response), DataSerializer.serialize(response), ttl);
    }

    @Override
    public void update(ProductResponse response) {
        redisTemplate.opsForValue().setIfPresent(generateKey(response), DataSerializer.serialize(response));
    }

    @Override
    public void delete(String productId) {
        redisTemplate.delete(generateKey(productId));
    }

    @Override
    public Optional<ProductResponse> read(String productId) {
        return Optional.ofNullable(
                        redisTemplate.opsForValue().get(generateKey(productId)))
                .map(json -> DataSerializer.deserialize(json, ProductResponse.class)
                );
    }

    private String generateKey(ProductResponse response) {
        return generateKey(response.id());
    }

    private String generateKey(String productId) {
        return KEY_FORMAT.formatted(productId);
    }
}
