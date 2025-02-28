package crisel.ayala.api.service.impl;

import crisel.ayala.api.externalServices.PercentageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MathOperationsServiceImplTest {

    @Mock
    private PercentageService percentageService;

    @InjectMocks
    private MathOperationsServiceImpl mathOperationsService;

    @Test
    void testProcess_SuccessfulCalculation() {
        when(percentageService.obtainPercentage()).thenReturn(10);
        Double result = mathOperationsService.process(100, 50).getRes();
        assertEquals(165.0, result);
    }

    @Test
    void testProcess_PercentageServiceUnavailable_ThrowsException() {
        when(percentageService.obtainPercentage()).thenReturn(-1);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            mathOperationsService.process(100, 50);
        });
        assertEquals("The service stopped unexpectedly, cached value not obtained", exception.getReason());
    }
}
