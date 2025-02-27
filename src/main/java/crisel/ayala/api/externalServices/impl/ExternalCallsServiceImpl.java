package crisel.ayala.api.externalServices.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crisel.ayala.api.externalServices.ExternalCallsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Service("externalCallsService")
public class ExternalCallsServiceImpl implements ExternalCallsService {

    @Value("${service.external.max-retries}")
    private int maxAttemps;

    private final WebClient webClient = WebClient.create();

    @Override
    public Object processGetRequest(String uri, Class<?> clazz) {
        int attempt = 0;
        while (attempt++ < maxAttemps) {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet request = new HttpGet(uri);
            request.addHeader("Accept", "application/json");

            try {
                CloseableHttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode != 200) {
                    log.error("Calling to "+ uri + " returned an error, attempt #"+ attempt);
                    log.error(responseBody);
                    continue;
                }
                return mapToObject(responseBody, clazz);

                } catch (Exception e) {
                    log.error("Calling to [{}] returned an error, attempt #{}",uri,attempt);
                    log.error(e.getMessage());
                    if (attempt == maxAttemps) {
                        log.error("You have reached the maximum number of retries calling to external API "+uri);
                        return null;
                      }
                }
        }
        return null;
    }
    private Object mapToObject(String responseBody, Class<?> clazz) throws JsonProcessingException {
        log.info("Mapping response to " + clazz.toString());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, clazz);
        } catch (JsonProcessingException ex) {
            log.error("Error when parsing a object from external service ", ex);
            throw ex;
        }
    }
}
