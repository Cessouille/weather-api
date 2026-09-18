package fr.cours.weather.api.infra.out.metnorway;

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

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.weather", name = "provider", havingValue = "met-norway")
public class MetNorwayForecastClient implements ForecastClient {

    private final RestClient metNorwayApiHttpClient;

    @Override
    public Forecast getForecast(double latitude, double longitude) {
        MetNorwayResponse response = metNorwayApiHttpClient.get()
                .uri(uriBuilder -> uriBuilder.path("/compact")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .retrieve()
                .body(MetNorwayResponse.class);

        return toForecast(latitude, longitude, response);
    }

    private Forecast toForecast(double latitude, double longitude, MetNorwayResponse response) {
        if (response == null || response.properties() == null || response.properties().timeseries() == null) {
            return new Forecast(latitude, longitude, List.of());
        }
        List<HourlyForecast> hourly = response.properties().timeseries().stream()
                .map(step -> new HourlyForecast(step.time(), step.data().instant().details().airTemperature()))
                .toList();
        return new Forecast(latitude, longitude, hourly);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MetNorwayResponse(MetNorwayProperties properties) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MetNorwayProperties(List<MetNorwayTimeStep> timeseries) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MetNorwayTimeStep(String time, MetNorwayData data) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MetNorwayData(MetNorwayInstant instant) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MetNorwayInstant(MetNorwayDetails details) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MetNorwayDetails(@JsonProperty("air_temperature") Double airTemperature) {
    }
}
