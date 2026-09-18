package fr.cours.weather.api.domain.model;

public record HourlyForecast(
        String time,
        double temperatureCelsius
) {
}
