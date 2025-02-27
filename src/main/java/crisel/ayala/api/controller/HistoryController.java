package crisel.ayala.api.controller;

import crisel.ayala.api.model.HistoryCall;
import crisel.ayala.api.service.HistoryCallService;
import crisel.ayala.api.utilities.ErrorSkeletonExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Tag(name = "Call history", description = "Call history endpoints")
public class HistoryController {

    private final HistoryCallService historyCallService;

    @GetMapping()
    @Operation(summary = "History", description = "Given pagination parameters, List the request history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History Obtained"),
            @ApiResponse(responseCode = "400", description = "Unsupported data type",
                    content = @Content(schema = @Schema(implementation = ErrorSkeletonExample.class))),
            @ApiResponse(responseCode = "400", description = "Parameter not present",
                    content = @Content(schema = @Schema(implementation = ErrorSkeletonExample.class))),
            @ApiResponse(responseCode = "429", description = "Exceeds the limit of requests per minute"),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content(schema = @Schema(implementation = ErrorSkeletonExample.class))),
            @ApiResponse(responseCode = "503", description = "The percentage value could not be obtained",
                    content = @Content(schema = @Schema(implementation = ErrorSkeletonExample.class)))
    })
    public Mono<ResponseEntity<Page<HistoryCall>>> getAllHistoryCalls(@ParameterObject @PageableDefault(page = 0, size = 10)
                                                                     @SortDefault.SortDefaults({
                                                                            @SortDefault(sort = "timeCreated",
                                                                                    direction = Sort.Direction.ASC)})
                                                                          Pageable pageable) {

        Page historyCalls = historyCallService.listHistoryCall(pageable);

        if (historyCalls.isEmpty()) {
            return Mono.just(ResponseEntity.noContent().build());
        }
        return Mono.just(ResponseEntity.ok(historyCalls));
    }
}
