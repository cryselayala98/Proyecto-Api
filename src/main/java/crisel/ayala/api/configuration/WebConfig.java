package crisel.ayala.api.configuration;

import crisel.ayala.api.service.HistoryCallService;
import crisel.ayala.api.utilities.interceptor.RequestHistoryHandlerFilter;
import crisel.ayala.api.utilities.interceptor.RequestsPerMinuteHandlerFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "web-config")
public class WebConfig {

    private List<String> excludedPathsForFiltering;

    @Bean
    public WebFilter requestsPerMinuteHandlerFilter() {
        return new RequestsPerMinuteHandlerFilter(this);
    }

    @Bean
    public WebFilter requestHistoryFilter(HistoryCallService historyCallService) {
        RequestHistoryHandlerFilter filter = new RequestHistoryHandlerFilter(historyCallService, this);
        return filter;
    }

    public List<String> getExcludedPaths() {
        return excludedPathsForFiltering;
    }

    public void setExcludedPaths(List<String> excludedPathsForFiltering) {
        this.excludedPathsForFiltering = excludedPathsForFiltering;
    }
}
