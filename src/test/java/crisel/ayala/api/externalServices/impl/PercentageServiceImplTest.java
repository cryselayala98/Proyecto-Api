package crisel.ayala.api.externalServices.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import crisel.ayala.api.cache.service.PercentageCacheService;
import crisel.ayala.api.entity.Percentage;
import crisel.ayala.api.externalServices.ExternalCallsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PercentageServiceImplTest {

    @Mock
    private ExternalCallsService externalCallsService;

    @Mock
    private PercentageCacheService percentageCacheService;

    @InjectMocks
    private PercentageServiceImpl percentageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(percentageService, "baseUrl", "http://api.example.com");
        ReflectionTestUtils.setField(percentageService, "percentageEndpoint", "/percentage");
        ReflectionTestUtils.setField(percentageService, "paramKey", "?key=123");
    }

    @Test
    void testObtainPercentage_SuccessfulExternalCall() {
        Percentage mockPercentage = new Percentage();
        mockPercentage.setValue(10);
        when(externalCallsService.processGetRequest(anyString(), eq(Percentage.class)))
                .thenReturn(mockPercentage);

        int result = percentageService.obtainPercentage();

        assertEquals(10, result);
        verify(percentageCacheService).save(mockPercentage);
    }

    @Test
    void testObtainPercentage_NullExternalCall_FallbackToCache() {

        when(externalCallsService.processGetRequest(anyString(), eq(Percentage.class)))
                .thenReturn(null);

        Percentage cachedPercentage = new Percentage();
        cachedPercentage.setValue(20);
        when(percentageCacheService.find(1L)).thenReturn(cachedPercentage);

        int result = percentageService.obtainPercentage();

        assertEquals(20, result);
    }

    @Test
    void testObtainPercentage_NoExternalCall_NoCache_ReturnsNegativeOne() {

        when(externalCallsService.processGetRequest(anyString(), eq(Percentage.class)))
                .thenReturn(null);
        when(percentageCacheService.find(1L)).thenReturn(null);

        int result = percentageService.obtainPercentage();

        assertEquals(-1, result);
    }
}
