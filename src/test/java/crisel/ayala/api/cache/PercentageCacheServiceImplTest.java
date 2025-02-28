package crisel.ayala.api.cache;

import crisel.ayala.api.cache.repository.PercentageCacheRepository;
import crisel.ayala.api.cache.service.impl.PercentageCacheServiceImpl;
import crisel.ayala.api.entity.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PercentageCacheServiceImplTest {

    @Mock
    private PercentageCacheRepository percentageCacheRepository;

    @InjectMocks
    private PercentageCacheServiceImpl<Percentage> percentageCacheService;


    private Percentage mockPercentage;

    @BeforeEach
    void setUp() {
        mockPercentage = new Percentage();
    }

    @Test
    void testSave_ShouldReturnSavedPercentage() {
        when(percentageCacheRepository.save(mockPercentage)).thenReturn(mockPercentage);

        Percentage result = percentageCacheService.save(mockPercentage);

        assertNotNull(result);
        assertEquals(mockPercentage, result);
        verify(percentageCacheRepository, times(1)).save(mockPercentage);
    }

    @Test
    void testFind_ShouldReturnPercentageIfExists() {
        Long id = 1L;
        when(percentageCacheRepository.findById(String.valueOf(id))).thenReturn(mockPercentage);

        Percentage result = percentageCacheService.find(id);

        assertNotNull(result);
        assertEquals(mockPercentage, result);
        verify(percentageCacheRepository, times(1)).findById(String.valueOf(id));
    }

    @Test
    void testFind_ShouldReturnNullIfNotExists() {
        Long id = 1L;
        when(percentageCacheRepository.findById(String.valueOf(id))).thenReturn(null);

        Percentage result = percentageCacheService.find(id);

        assertNull(result);
        verify(percentageCacheRepository, times(1)).findById(String.valueOf(id));
    }

    @Test
    void testDelete_ShouldCallRepositoryDelete() {
        Long id = 1L;

        when(percentageCacheRepository.deleteById(String.valueOf(id))).thenReturn(true);

        percentageCacheService.delete(id);

        verify(percentageCacheRepository, times(1)).deleteById(String.valueOf(id));
    }
}
