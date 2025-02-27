package crisel.ayala.api.utilities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Example of error response")
public class ErrorSkeletonExample {

    @Schema(description = "Error message", example = "Any error message")
    private String message;

    @Schema(description = "Error trace", example = "Any error trace")
    private String trace;

}
