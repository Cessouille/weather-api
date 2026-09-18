package fr.cours.weather.api.domain.model;

public record Geocoding(
        String name,
        double latitude,
        double longitude
) {
}
