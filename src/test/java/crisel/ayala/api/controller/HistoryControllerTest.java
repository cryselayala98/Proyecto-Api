package crisel.ayala.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import crisel.ayala.api.model.HistoryCall;
import crisel.ayala.api.service.HistoryCallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

    @Mock
    private HistoryCallService historyCallService;

    @InjectMocks
    private HistoryController historyController;

    private Page<HistoryCall> mockPage;

    @BeforeEach
    void setUp() {
        HistoryCall historyCall = new HistoryCall(); // Assuming HistoryCall has a default constructor
        List<HistoryCall> historyList = Collections.singletonList(historyCall);
        mockPage = new PageImpl<>(historyList);
    }

    @Test
    void testGetAllHistoryCalls_ReturnsHistoryPage() {
        Pageable pageable = mock(Pageable.class);

        when(historyCallService.listHistoryCall(pageable)).thenReturn(mockPage);

        Mono<ResponseEntity<Page<HistoryCall>>> response = historyController.getAllHistoryCalls(pageable);

        assertNotNull(response);
        assertEquals(200, response.block().getStatusCodeValue());
        assertEquals(mockPage, response.block().getBody());

        verify(historyCallService, times(1)).listHistoryCall(pageable);
    }

    @Test
    void testGetAllHistoryCalls_ReturnsNoContent() {
        Pageable pageable = mock(Pageable.class);

        when(historyCallService.listHistoryCall(pageable)).thenReturn(Page.empty());

        Mono<ResponseEntity<Page<HistoryCall>>> response = historyController.getAllHistoryCalls(pageable);

        assertNotNull(response);
        assertEquals(204, response.block().getStatusCodeValue());

        verify(historyCallService, times(1)).listHistoryCall(pageable);
    }

    @Test
    void testGetAllHistoryCalls_WhenServiceThrowsException_ShouldReturn500() {
        Pageable pageable = mock(Pageable.class);

        when(historyCallService.listHistoryCall(pageable)).thenThrow(new RuntimeException("Internal Server Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> historyController.getAllHistoryCalls(pageable).block());

        assertEquals("Internal Server Error", exception.getMessage());
        verify(historyCallService, times(1)).listHistoryCall(pageable);
    }
}

