package fr.cours.weather.api.infra.out.openmeteo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.HourlyForecast;
import fr.cours.weather.api.domain.port.out.ForecastClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.weather", name = "provider", havingValue = "open-meteo", matchIfMissing = true)
public class OpenMeteoForecastClient implements ForecastClient {

    private final RestClient openMeteoApiHttpClient;

    @Override
    public Forecast getForecast(double latitude, double longitude) {
        OpenMeteoResponse response = openMeteoApiHttpClient.get()
                .uri(uriBuilder -> uriBuilder.path("/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("hourly", "temperature_2m")
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);

        return toForecast(latitude, longitude, response);
    }

    private Forecast toForecast(double latitude, double longitude, OpenMeteoResponse response) {
        if (response == null || response.hourly() == null) {
            return new Forecast(latitude, longitude, List.of());
        }
        List<String> times = response.hourly().time();
        List<Double> temperatures = response.hourly().temperature();
        List<HourlyForecast> hourly = IntStream.range(0, times.size())
                .mapToObj(i -> new HourlyForecast(times.get(i), temperatures.get(i)))
                .toList();
        return new Forecast(latitude, longitude, hourly);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenMeteoResponse(OpenMeteoHourly hourly) {
    }

    private record OpenMeteoHourly(
            List<String> time,
            @JsonProperty("temperature_2m") List<Double> temperature
    ) {
    }
}
