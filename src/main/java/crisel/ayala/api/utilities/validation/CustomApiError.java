package crisel.ayala.api.utilities.validation;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class CustomApiError {
    private HttpStatus status;
    private String message;
}
