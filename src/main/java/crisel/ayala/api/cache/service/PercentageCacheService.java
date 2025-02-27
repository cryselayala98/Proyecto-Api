package crisel.ayala.api.cache.service;

import crisel.ayala.api.entity.Percentage;

public interface PercentageCacheService {

    public Percentage save(Percentage percentage);

    public Percentage find(Long id);

    public void delete(long id);

}
