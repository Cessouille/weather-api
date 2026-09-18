package fr.cours.weather.api.infra.out.openmeteo;

import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.port.out.ForecastClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OpenMeteoForecastClient implements ForecastClient {

    private final RestClient openMeteoApiHttpClient;

    @Override
    public Forecast getForecast(double latitude, double longitude) {
        return openMeteoApiHttpClient.get()
                .uri(uriBuilder -> uriBuilder.path("/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("hourly", "temperature_2m")
                        .build())
                .retrieve()
                .body(Forecast.class);
    }
}
