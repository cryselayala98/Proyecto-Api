package crisel.ayala.api.model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="history_call")
@Schema(description = "Object history")
public class HistoryCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "History id", example = "1")
    private Long id;


    @NotNull
    @Column(name="time_created", columnDefinition = "TIMESTAMP")
    @Schema(description = "Timestamp")
    private LocalDateTime timeCreated;

    @NotBlank
    @Column(name="url")
    @Schema(description = "Uru invoked", example = "http://localhost:8080/hellofriends")
    private String url;

    @NotBlank
    @Column(name="method")
    @Schema(description = "method", example = "GET")
    private String method;

    @Column(name="ipFrom")
    @Schema(description = "ip from")
    private String ipFrom;

    @Column(name="params")
    @Schema(description = "params", example = "num1=45&num2=5")
    private String params;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "response_id", referencedColumnName = "id")
    @Schema(description = "Response received")
    private Response response;









}
