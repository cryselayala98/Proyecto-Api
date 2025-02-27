package crisel.ayala.api.cache.repository.impl;

import crisel.ayala.api.cache.model.RedisEntity;
import crisel.ayala.api.cache.repository.PercentageCacheRepository;
import crisel.ayala.api.cache.repository.RedisRepository;
import crisel.ayala.api.entity.Percentage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public class PercentageCacheRepositoryImpl<T extends Percentage> extends RedisRepository<T> implements PercentageCacheRepository, Serializable {

    @Value("${spring.resolved.cache.ttl:30}")
    private int timeExpirationInMinutes;
    private static final String HASH_FOR_PERCENTAGE = "PERCENTAGE";

    public PercentageCacheRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    public Percentage save(Percentage percentage) {

        saveHash(HASH_FOR_PERCENTAGE, String.valueOf(percentage.getId()), (T) percentage, timeExpirationInMinutes * 60);
        return percentage;
    }

    public Percentage findById(String id) {
        Optional<RedisEntity<T>> percentageOpt = findHash(HASH_FOR_PERCENTAGE, id);
        if (!percentageOpt.isPresent()) return null;

        return percentageOpt.get().getValue();
    }

    public boolean existsById(String id) {
        return findHash(HASH_FOR_PERCENTAGE, id).isPresent();
    }

    public boolean deleteById(String id) {
        return deleteHash(HASH_FOR_PERCENTAGE, id);
    }
}