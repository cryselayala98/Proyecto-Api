package crisel.ayala.api.cache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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