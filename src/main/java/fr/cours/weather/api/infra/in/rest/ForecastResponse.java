package fr.cours.weather.api.infra.in.rest;

import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.HourlyForecast;

import java.util.List;

public record ForecastResponse(
        String address,
        double latitude,
        double longitude,
        List<HourlyForecast> hourly
) {

    public static ForecastResponse of(String address, Forecast forecast) {
        return new ForecastResponse(address, forecast.latitude(), forecast.longitude(), forecast.hourly());
    }
}
