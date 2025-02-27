package crisel.ayala.api.controller;


import crisel.ayala.api.model.ObjectOperation;
import crisel.ayala.api.service.MathOperationsService;
import crisel.ayala.api.utilities.ErrorSkeletonExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
@Tag(name = "Operations", description = "Endpoints for managing operations")
public class MathOperationsController {

    private final MathOperationsService mathOperationsService;


    @GetMapping(value = "/sum")
    @Operation(summary = "Sum of two numbers", description = "Given two numbers, add them together and add a percentage from an external supplier to their total.")
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
    public ResponseEntity<ObjectOperation> sum(
            @Parameter(description = "numerical value", example = "5") @RequestParam int num1,
            @Parameter(description = "numerical value", example = "5") @RequestParam int num2) {

        ObjectOperation res = this.mathOperationsService.process(num1, num2);

        return  ResponseEntity.ok(res);
    }

    }
