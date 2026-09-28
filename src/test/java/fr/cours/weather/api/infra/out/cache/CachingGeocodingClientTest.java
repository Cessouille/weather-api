package fr.cours.weather.api.infra.out.cache;

import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.out.GeocodingCache;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CachingGeocodingClientTest {

    @Mock
    private GeocodingClient delegate;

    @Mock
    private GeocodingCache cache;

    private CachingGeocodingClient cachingGeocodingClient;

    @BeforeEach
    void setUp() {
        cachingGeocodingClient = new CachingGeocodingClient(delegate, cache);
    }

    @Test
    void geocode_callsDelegateAndStoresResult_whenCacheIsEmpty() {
        Geocoding geocoding = new Geocoding("Alès", 44.1277, 4.0817);
        when(cache.get("Alès")).thenReturn(Optional.empty());
        when(delegate.geocode("Alès")).thenReturn(Optional.of(geocoding));

        Optional<Geocoding> result = cachingGeocodingClient.geocode("Alès");

        assertThat(result).contains(geocoding);
        verify(cache).put("Alès", geocoding);
    }

    @Test
    void geocode_returnsCachedValueWithoutCallingDelegate_whenCacheHasEntry() {
        Geocoding geocoding = new Geocoding("Alès", 44.1277, 4.0817);
        when(cache.get("Alès")).thenReturn(Optional.of(geocoding));

        Optional<Geocoding> result = cachingGeocodingClient.geocode("Alès");

        assertThat(result).contains(geocoding);
        verifyNoInteractions(delegate);
    }

    @Test
    void geocode_doesNotCacheEmptyResult_whenDelegateFindsNothing() {
        when(cache.get("Nowhereville")).thenReturn(Optional.empty());
        when(delegate.geocode("Nowhereville")).thenReturn(Optional.empty());

        Optional<Geocoding> result = cachingGeocodingClient.geocode("Nowhereville");

        assertThat(result).isEmpty();
        verify(cache, never()).put(eq("Nowhereville"), any());
    }

    @Test
    void geocode_callsDelegateSeparately_forDifferentAddresses() {
        Geocoding ales = new Geocoding("Alès", 44.1277, 4.0817);
        Geocoding paris = new Geocoding("Paris", 48.85, 2.35);
        when(cache.get("Alès")).thenReturn(Optional.empty());
        when(cache.get("Paris")).thenReturn(Optional.empty());
        when(delegate.geocode("Alès")).thenReturn(Optional.of(ales));
        when(delegate.geocode("Paris")).thenReturn(Optional.of(paris));

        assertThat(cachingGeocodingClient.geocode("Alès")).contains(ales);
        assertThat(cachingGeocodingClient.geocode("Paris")).contains(paris);

        verify(delegate).geocode("Alès");
        verify(delegate).geocode("Paris");
    }
}
