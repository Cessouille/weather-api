package fr.cours.weather.api.infra.out.demo;

import fr.cours.weather.api.domain.model.Forecast;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoForecastClientTest {

    private final DemoForecastClient client = new DemoForecastClient();

    @Test
    void getForecast_returnsFixedSimulatedHourlyData() {
        Forecast forecast = client.getForecast(44.1279, 4.0817);

        assertThat(forecast.latitude()).isEqualTo(44.1279);
        assertThat(forecast.longitude()).isEqualTo(4.0817);
        assertThat(forecast.hourly()).hasSize(1);
        assertThat(forecast.hourly().getFirst().time()).isEqualTo("2025-06-10T14:00:00Z");
        assertThat(forecast.hourly().getFirst().temperatureCelsius()).isEqualTo(24.3);
    }
}
