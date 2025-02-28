package crisel.ayala.api.cache.model;

import crisel.ayala.api.entity.Percentage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisEntity<T extends Serializable> implements Serializable {

    private String hash;
    private String key;
    private T value;

}