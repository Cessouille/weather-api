package fr.cours.weather.api.infra.out.demo;

import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.HourlyForecast;
import fr.cours.weather.api.domain.port.out.ForecastClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("demo")
public class DemoForecastClient implements ForecastClient {

    private static final String SIMULATED_TIME = "2025-06-10T14:00:00Z";
    private static final double SIMULATED_TEMPERATURE_CELSIUS = 24.3;

    @Override
    public Forecast getForecast(double latitude, double longitude) {
        return new Forecast(latitude, longitude, List.of(new HourlyForecast(SIMULATED_TIME, SIMULATED_TEMPERATURE_CELSIUS)));
    }
}
