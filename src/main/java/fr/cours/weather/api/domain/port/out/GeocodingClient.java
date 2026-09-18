package fr.cours.weather.api.domain.port.out;

import fr.cours.weather.api.domain.model.Geocoding;

import java.util.Optional;

public interface GeocodingClient {

    Optional<Geocoding> geocode(String cityName);
}
