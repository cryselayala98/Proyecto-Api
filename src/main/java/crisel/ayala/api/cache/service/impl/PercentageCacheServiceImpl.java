package crisel.ayala.api.cache.service.impl;

import crisel.ayala.api.cache.repository.PercentageCacheRepository;
import crisel.ayala.api.cache.service.PercentageCacheService;
import crisel.ayala.api.entity.Percentage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@RequiredArgsConstructor
@Service
@Slf4j
public class PercentageCacheServiceImpl<T extends Percentage> implements Serializable, PercentageCacheService {


    private final PercentageCacheRepository percentageCacheRepository;

    public Percentage save(Percentage percentage){
        log.info("Saving in cache..." + percentage.toString());
        Percentage percentageSave =  percentageCacheRepository.save(percentage);
        System.out.println(percentageSave);
        return percentageSave;
    }

    public Percentage find(Long id){
        return percentageCacheRepository.findById(String.valueOf(id));
    }

    public void delete(long id){
        percentageCacheRepository.deleteById(String.valueOf(id));
    }
}
