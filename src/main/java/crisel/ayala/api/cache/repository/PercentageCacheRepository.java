package crisel.ayala.api.cache.repository;

import crisel.ayala.api.entity.Percentage;

public interface PercentageCacheRepository {

    public Percentage save(Percentage percentage);

    public Percentage findById(String id);

    public boolean existsById(String id);

    public boolean deleteById(String id);
}
