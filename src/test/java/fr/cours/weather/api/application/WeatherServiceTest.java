package fr.cours.weather.api.application;

import fr.cours.weather.api.domain.exception.CityNotFoundException;
import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.model.Hourly;
import fr.cours.weather.api.domain.port.out.ForecastClient;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void getForecastForCity_delegatesToForecastClient_whenCityIsFound() {
        Geocoding geocoding = new Geocoding("Alès", "Alès, Gard, France", "44.1277", "4.0817");
        Forecast forecast = new Forecast(44.1277, 4.0817, "GMT", "GMT",
                new Hourly(List.of("2026-01-01T00:00"), List.of(12.3)));
        when(geocodingClient.geocode("Alès")).thenReturn(Optional.of(geocoding));
        when(forecastClient.getForecast(44.1277, 4.0817)).thenReturn(forecast);

        Forecast result = weatherService.getForecastForCity("Alès");

        assertThat(result).isEqualTo(forecast);
    }

    @Test
    void getForecastForCity_throwsCityNotFoundException_whenGeocodingHasNoMatch() {
        when(geocodingClient.geocode("Nowhereville")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> weatherService.getForecastForCity("Nowhereville"))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining("Nowhereville");

        verifyNoInteractions(forecastClient);
    }
}
