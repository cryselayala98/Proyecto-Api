package crisel.ayala.api.cache.repository;

import crisel.ayala.api.cache.model.RedisEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public abstract class RedisRepository<T extends Serializable> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOperations;
    private final HashOperations<String, String, Object> hashOperations;

    @Autowired
    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        valueOperations = redisTemplate.opsForValue();
        hashOperations = redisTemplate.opsForHash();
    }

    public boolean saveValue(final String key, final T value) {
        valueOperations.set(key, value);

        return true;
    }

    public boolean saveHash(final String hash, final String key, final T value) {
        try{
            hashOperations.put(hash, key, value);
        }catch (Exception ex) {
            //log.error("Error when saving in cache", ex);
        }

        return true;
    }

    public boolean saveHash(final String hash, final String key, final T value, final int expiresInSeconds) {
        boolean save = saveHash(hash, key, value);

        redisTemplate.expire(hash, Duration.ofMinutes(expiresInSeconds));

        List<Object> found = findByHash(hash);

        return save;
    }

    public Optional<Object> findValue(final String key) {
        Object obj = valueOperations.get(key);

        return Optional.ofNullable(obj);
    }

    public Optional<RedisEntity<T>> findHash(final String hash, final String key) {
        T value = (T) hashOperations.get(hash, key);

        return (value == null) ? Optional.empty() : Optional.of(new RedisEntity<T>(hash, key, value));
    }

    public List<Object> findByHash(final String hash) {
        return new ArrayList<>(hashOperations.entries(hash).entrySet());
    }

    public boolean deleteValue(final String key) {
        return redisTemplate.delete(key);
    }

    public boolean deleteHash(final String hash, final String key) {
        return hashOperations.delete(hash, key) > 0;
    }
}