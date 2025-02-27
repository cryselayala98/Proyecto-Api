package crisel.ayala.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${service.external.response.timeout}")
    private int apiResponseTimeout;

    @Bean
    public WebClient webClient() {
        return buildWebClient(apiResponseTimeout);
    }

    protected WebClient buildWebClient(int apiResponseTimeout) {
        WebClient.Builder builder = WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(getHttpClient(apiResponseTimeout)))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

        return builder.build();
    }

    protected HttpClient getHttpClient(int apiResponseTimeout) {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(apiResponseTimeout));
    }
}
