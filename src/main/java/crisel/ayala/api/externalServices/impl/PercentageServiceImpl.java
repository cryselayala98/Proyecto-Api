package crisel.ayala.api.externalServices.impl;

import crisel.ayala.api.cache.service.PercentageCacheService;
import crisel.ayala.api.entity.Percentage;
import crisel.ayala.api.externalServices.ExternalCallsService;
import crisel.ayala.api.externalServices.PercentageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service("percentageService")
@RequiredArgsConstructor
public class PercentageServiceImpl implements PercentageService {

    private final ExternalCallsService externalCallsService;
    private final PercentageCacheService percentageCacheService;

    @Value("${service.external.endpoint.url}")
    private String baseUrl;

    @Value("${service.external.endpoint.percentage}")
    private String percentageEndpoint;

    @Value("${service.external.key}")
    private String paramKey;

    @Override
    public int obtainPercentage() {
        String uri = this.baseUrl + this.percentageEndpoint + this.paramKey;

        Percentage percentage = ((Percentage) externalCallsService.processGetRequest(
                uri, Percentage.class));

        if(percentage != null) {
            savePercentageInCache(percentage);
            return percentage.getValue();
        }
        return getPercentageFromCache();
    }

    private Percentage savePercentageInCache(Percentage percentage){

        return percentageCacheService.save(percentage);
    }

    private int getPercentageFromCache() {
        Percentage percentage = percentageCacheService.find(1L);
        return percentage != null? percentage.getValue() : -1;
    }
}
