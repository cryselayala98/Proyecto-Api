package crisel.ayala.api.service;

import crisel.ayala.api.model.HistoryCall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryCallService {

    public HistoryCall save(HistoryCall historyCall);

    public Page<HistoryCall> listHistoryCall(Pageable pageable);

}
