package crisel.ayala.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Object of the result of the operation")
public class ObjectOperation {

    @Schema(description = "numerical value", example = "45")
    private int num1;

    @Schema(description = "numerical value", example = "55")
    private int num2;

    @Schema(description = "numerical value", example = "110.0")
    private Double res;

}
