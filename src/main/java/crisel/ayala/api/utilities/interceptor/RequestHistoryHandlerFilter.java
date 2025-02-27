package crisel.ayala.api.utilities.interceptor;

import crisel.ayala.api.configuration.WebConfig;
import crisel.ayala.api.model.HistoryCall;
import crisel.ayala.api.model.Response;
import crisel.ayala.api.service.HistoryCallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class RequestHistoryHandlerFilter implements WebFilter {

    private final HistoryCallService historyCallService;

    private final WebConfig webConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {


        String pathInvoke = exchange.getRequest().getURI().getPath();

        log.info("Starting analysis for {}", pathInvoke);
        if(pathIsExcluded(pathInvoke)){
            log.info("{} ignored {} for analysis", this.getClass().getName(), pathInvoke);
            return chain.filter(exchange);
        }


        HistoryCall historyCall = createHistoryDataAndSave(exchange);
        return chain.filter(exchange).
                onErrorResume(ex ->  handleException(exchange, ex, historyCall))
                .then(Mono.defer(() -> handleResponse(exchange, chain, historyCall)));
    }

    private Mono<Void> handleException(ServerWebExchange exchange, Throwable ex, HistoryCall historyCall) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String trace = ex.getMessage();

        if(ex instanceof ResponseStatusException){
            status = HttpStatus.resolve(((ResponseStatusException) ex).getStatusCode().value());
            trace = ((ResponseStatusException) ex).getReason();
            updateResponseAndSave(Optional.of((ResponseStatusException)ex), exchange, historyCall);
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonError = String.format("{\n\tmessage: \"%s\", \n\ttrace: \"%s\" \n}", status.toString(), trace);

        DataBuffer dataBuffer = exchange.getResponse()
                .bufferFactory()
                .wrap(jsonError.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, WebFilterChain chain, HistoryCall historyCall) {
        Response response = historyCall.getResponse();
        ServerHttpResponse httpResponse = exchange.getResponse();
        String message = "";
        DataBufferFactory bufferFactory = httpResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(httpResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody
                            .flatMap(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);

                                String responseBody = new String(content, StandardCharsets.UTF_8);
                                log.info("Intercepted Response: " + responseBody);


                                byte[] newContent = responseBody.toUpperCase().getBytes(StandardCharsets.UTF_8);
                                DataBuffer newBuffer = bufferFactory.wrap(newContent);

                                return Mono.just(newBuffer);
                            })
                    );
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return super.writeAndFlushWith(body);
            }
        };

        if(HttpStatus.Series.valueOf(response.getCode()) != HttpStatus.Series.SERVER_ERROR &&
                HttpStatus.Series.valueOf(response.getCode()) != HttpStatus.Series.CLIENT_ERROR){
            response = updateResponseAndSave(Optional.of(null), exchange, historyCall);
        }

        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .doOnSuccess(aVoid -> System.out.println("Response processing completed"));
    }

    private HistoryCall createHistoryDataAndSave(ServerWebExchange exchange){
        log.info("Recolecting data from request....");
        HistoryCall historyCall = this.constructHistoryData(exchange);
        return historyCallService.save(historyCall);
    }

    private HistoryCall constructHistoryData(ServerWebExchange exchange) {

        String fromIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (fromIp == null) {
            if (exchange.getRequest().getRemoteAddress() != null) {
                fromIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            } else fromIp = "unknown";
        }

        Response response = constructResponse();
        return HistoryCall.builder()
                        .method(exchange.getRequest().getMethod().name())
                        .url(exchange.getRequest().getURI().toString())
                        .params(exchange.getRequest().getQueryParams()
                                .entrySet()
                                .stream()
                                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                                .collect(Collectors.joining("&")))
                        .ipFrom(fromIp)
                        .timeCreated(LocalDateTime.now())
                        .response(response)
                        .build();
    }

    private Response updateResponseAndSave(Optional<ResponseStatusException> ex, ServerWebExchange exchange, HistoryCall historyCall){
        Response responseModel = constructResponse(ex, exchange);
        responseModel.setId(historyCall.getResponse().getId());
        historyCall.setResponse(responseModel);

        log.info("Updating History when Response received...");
        historyCallService.save(historyCall);

        return historyCall.getResponse();
    }
    private Response constructResponse(){
        return Response.builder()
                .code(200)
                .message("OK")
                .build();
    }
    private Response constructResponse(Optional<ResponseStatusException> ex, ServerWebExchange exchange) {

        int codeStatus = exchange.getResponse().getStatusCode().value();
        HttpStatus status = HttpStatus.resolve(codeStatus);
        String message = exchange.toString();

        if (ex.isPresent()) {
            codeStatus = ex.get().getStatusCode().value();
            status = HttpStatus.resolve(codeStatus);
            message = ex.get().getMessage();
        }

        return Response.builder()
                .code(codeStatus)
                .message(status.toString())
                .trace(message)
                .build();
    }

    private boolean pathIsExcluded(String pathInvoke){
        List<String> excludedPaths = webConfig.getExcludedPaths();
        return excludedPaths != null && excludedPaths.stream().anyMatch(pathInvoke::contains);
    }
}

