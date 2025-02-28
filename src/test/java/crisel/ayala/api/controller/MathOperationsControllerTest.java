package crisel.ayala.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import crisel.ayala.api.model.ObjectOperation;
import crisel.ayala.api.service.MathOperationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class MathOperationsControllerTest {

    @Mock
    private MathOperationsService mathOperationsService;

    @InjectMocks
    private MathOperationsController mathOperationsController;

    private ObjectOperation mockOperation;

    @BeforeEach
    void setUp() {
        mockOperation = new ObjectOperation();
        mockOperation.setRes(11.0);
    }

    @Test
    void testSum_ReturnsCorrectResponse() {
        int num1 = 5;
        int num2 = 5;

        when(mathOperationsService.process(num1, num2)).thenReturn(mockOperation);

        ResponseEntity<ObjectOperation> response = mathOperationsController.sum(num1, num2);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockOperation, response.getBody());

        verify(mathOperationsService, times(1)).process(num1, num2);
    }

    @Test
    void testSum_WhenServiceThrowsException_ShouldReturn500() {
        int num1 = 5;
        int num2 = 5;

        when(mathOperationsService.process(num1, num2)).thenThrow(new RuntimeException("Internal Server Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> mathOperationsController.sum(num1, num2));

        assertEquals("Internal Server Error", exception.getMessage());
        verify(mathOperationsService, times(1)).process(num1, num2);
    }

    @Test
    void testSum_WhenInvalidInput_ShouldReturn400() {
        int num1 = Integer.MAX_VALUE;
        int num2 = Integer.MAX_VALUE;

        when(mathOperationsService.process(num1, num2)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Input"));

        Exception exception = assertThrows(ResponseStatusException.class, () -> mathOperationsController.sum(num1, num2));

        assertEquals(400, ((ResponseStatusException) exception).getStatusCode().value());
        assertEquals("Invalid Input", ((ResponseStatusException) exception).getReason());
        verify(mathOperationsService, times(1)).process(num1, num2);
    }
}

