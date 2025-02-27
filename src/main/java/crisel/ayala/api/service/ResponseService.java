package crisel.ayala.api.service;

import crisel.ayala.api.model.HistoryCall;
import crisel.ayala.api.model.Response;
import reactor.core.publisher.Mono;

public interface ResponseService {

    public Response save(Response response);
}
