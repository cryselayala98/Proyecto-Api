package crisel.ayala.api.service.impl;
import crisel.ayala.api.externalServices.PercentageService;
import crisel.ayala.api.model.ObjectOperation;
import crisel.ayala.api.service.MathOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service("mathOperationsService")
@RequiredArgsConstructor
public class MathOperationsServiceImpl implements MathOperationsService {

    private final PercentageService percentageService;

    @Override
    public ObjectOperation process(int num1, int num2) {
        double res = this.sum(num1, num2);
        return ObjectOperation.builder()
                .num1(num1)
                .num2(num2)
                .res(this.sumPercentage(res))
                .build();
    }

    private int sum(int num1, int num2) {
        return num1 + num2;
    }

    private double sumPercentage(double num) {
        int percentage = percentageService.obtainPercentage();

        if(percentage < 0 ) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The service stopped unexpectedly, cached value not obtained");
        }

        return num + (num/percentage);
    }
}
