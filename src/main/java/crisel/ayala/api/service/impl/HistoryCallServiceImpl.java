package crisel.ayala.api.service.impl;

import crisel.ayala.api.model.HistoryCall;
import crisel.ayala.api.repository.HistoryCallRepository;
import crisel.ayala.api.service.HistoryCallService;
import crisel.ayala.api.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryCallServiceImpl implements HistoryCallService {

    private final HistoryCallRepository historyCallRepository;
    private final ResponseService responseService;

    @Override
    public HistoryCall save(HistoryCall historyCall){
        log.info("Saving History Call..." + historyCall.toString());
        return historyCallRepository.save(historyCall);
    }

    @Override
    public Page<HistoryCall> listHistoryCall(Pageable pageable) {
        return historyCallRepository.findAll(pageable);
    }


}
