package crisel.ayala.api.service.impl;

import crisel.ayala.api.model.HistoryCall;
import crisel.ayala.api.model.Response;
import crisel.ayala.api.repository.ResponseRepository;
import crisel.ayala.api.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;

    @Override
    public Response save(Response response){
        return responseRepository.save(response);
    }

}
