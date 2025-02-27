package crisel.ayala.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="response")
@Schema(description = "Response")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Response id", example = "1")
    private Long id;

    @NotNull
    @Column(name="Code", nullable=false)
    @Schema(description = "code HTTP", example = "418")
    private int code;

    @Column(name="message")
    @Schema(description = "Message in case of error", example = "418 I'm a teapot :)")
    private String message;

    @Column(name="trace")
    @Schema(description = "Message trace in case of error", example = "java.lang.nullpointerexception")
    private String trace;
}
