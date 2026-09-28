package fr.cours.weather.api.application;

import fr.cours.weather.api.domain.exception.CityNotFoundException;
import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.model.HourlyForecast;
import fr.cours.weather.api.domain.port.out.ForecastClient;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private ForecastClient forecastClient;

    @Mock
    private GeocodingClient demoGeocodingClient;

    @Mock
    private ForecastClient demoForecastClient;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(geocodingClient, forecastClient, demoGeocodingClient, demoForecastClient);
    }

    @Test
    void getForecastForCity_delegatesToRealClients_whenDemoIsFalse() {
        Geocoding geocoding = new Geocoding("Alès", 44.1277, 4.0817);
        Forecast forecast = new Forecast(44.1277, 4.0817, List.of(new HourlyForecast("2026-01-01T00:00", 12.3)));
        when(geocodingClient.geocode("Alès")).thenReturn(Optional.of(geocoding));
        when(forecastClient.getForecast(44.1277, 4.0817)).thenReturn(forecast);

        Forecast result = weatherService.getForecastForCity("Alès", false);

        assertThat(result).isEqualTo(forecast);
        verifyNoInteractions(demoGeocodingClient, demoForecastClient);
    }

    @Test
    void getForecastForCity_throwsCityNotFoundException_whenAddressHasNoMatchAndDemoIsFalse() {
        when(geocodingClient.geocode("Nowhereville")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> weatherService.getForecastForCity("Nowhereville", false))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining("Nowhereville");

        verifyNoInteractions(forecastClient, demoGeocodingClient, demoForecastClient);
    }

    @Test
    void getForecastForCity_delegatesToDemoClientsOnly_whenDemoIsTrue() {
        Geocoding demoGeocoding = new Geocoding("Alès", 44.1279, 4.0817);
        Forecast demoForecast = new Forecast(44.1279, 4.0817, List.of(new HourlyForecast("2025-06-10T14:00:00Z", 24.3)));
        when(demoGeocodingClient.geocode("Alès")).thenReturn(Optional.of(demoGeocoding));
        when(demoForecastClient.getForecast(44.1279, 4.0817)).thenReturn(demoForecast);

        Forecast result = weatherService.getForecastForCity("Alès", true);

        assertThat(result).isEqualTo(demoForecast);
        verifyNoInteractions(geocodingClient, forecastClient);
    }
}
