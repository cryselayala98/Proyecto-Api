package crisel.ayala.api.repository;


import crisel.ayala.api.model.HistoryCall;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryCallRepository extends CrudRepository<HistoryCall, Long>,
        PagingAndSortingRepository<HistoryCall, Long> {

}
