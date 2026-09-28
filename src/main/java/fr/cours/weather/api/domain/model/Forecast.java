package fr.cours.weather.api.domain.model;

import java.util.List;

public record Forecast(
        double latitude,
        double longitude,
        List<HourlyForecast> hourly
) {
}
