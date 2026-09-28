package fr.cours.weather.api.domain.port.out;

import fr.cours.weather.api.domain.model.Forecast;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contract every ForecastClient implementation must satisfy. Each adapter's
 * test subclass stubs its own HTTP responses via the given* hooks below.
 */
public abstract class ForecastClientContractTest {

    protected abstract ForecastClient client();

    protected abstract void givenForecastResponse();

    protected abstract void givenEmptyForecastResponse();

    @Test
    void getForecast_returnsHourlyTemperatures_forValidCoordinates() {
        givenForecastResponse();

        Forecast forecast = client().getForecast(44.1277, 4.0817);

        assertThat(forecast.latitude()).isEqualTo(44.1277);
        assertThat(forecast.longitude()).isEqualTo(4.0817);
        assertThat(forecast.hourly()).isNotEmpty();
        assertThat(forecast.hourly().getFirst().temperatureCelsius()).isBetween(-60.0, 60.0);
    }

    @Test
    void getForecast_returnsEmptyHourly_whenProviderHasNoData() {
        givenEmptyForecastResponse();

        Forecast forecast = client().getForecast(44.1277, 4.0817);

        assertThat(forecast.hourly()).isEmpty();
    }
}
