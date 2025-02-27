package crisel.ayala.api.utilities.interceptor;

import crisel.ayala.api.configuration.WebConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class RequestsPerMinuteHandlerFilter implements WebFilter {

    @Value("${service.endpoint.max-requests-per-minute:3}")
    private int requestLimit;
    private static final long TIME_MILLIS = 60_000;
    private final Map<String, LinkedList<Long>> requestLogsByIp = new ConcurrentHashMap<>();
    private final WebConfig webConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String pathInvoke = exchange.getRequest().getURI().getPath();

        log.info("Starting analysis for {}", pathInvoke);
        if(pathIsExcluded(pathInvoke)){
            log.info("{} ignored {} for analysis", this.getClass().getName(), pathInvoke);
            return chain.filter(exchange);
        }

        String ipFrom = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

        long now = Instant.now().toEpochMilli();

        requestLogsByIp.putIfAbsent(ipFrom, new LinkedList<>());
        LinkedList<Long> clientTimestamps = requestLogsByIp.get(ipFrom);

        synchronized (clientTimestamps) {

            resetHistoryRequests(clientTimestamps, now);

            if (numberOfRequestsIsExceeded(clientTimestamps.size())) {
                log.error("The client {} exceeded the number of requests per minute", ipFrom);
                String jsonError = String.format("{\n\tmessage: \"%s\", \n\ttrace: \"%s\" \n}", "You have exceeded the number of requests per minute. Pleease try again later.", "\"\"");

                DataBuffer dataBuffer = exchange.getResponse()
                        .bufferFactory()
                        .wrap(jsonError.getBytes(StandardCharsets.UTF_8));

                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().writeWith(Mono.just(dataBuffer));

                return exchange.getResponse().setComplete();
            }
            clientTimestamps.addLast(now);
        }

        return chain.filter(exchange);
    }


    /*
    * Borra el registro de llamadas del usuario cuando se hicieron hace más de un minuto
    * */
    private void resetHistoryRequests(LinkedList<Long> clientTimestamps, long time){
        while (!clientTimestamps.isEmpty() && (time - clientTimestamps.peekFirst() > TIME_MILLIS)) {
            clientTimestamps.pollFirst();
        }
    }

    private boolean numberOfRequestsIsExceeded(int currentNumberOfRequests){
        return currentNumberOfRequests >= requestLimit;
    }

    private boolean pathIsExcluded(String pathInvoke){
        List<String> excludedPaths = webConfig.getExcludedPaths();
        return excludedPaths != null && excludedPaths.stream().anyMatch(pathInvoke::contains);
    }
}
