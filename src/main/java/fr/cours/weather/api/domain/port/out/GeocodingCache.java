package fr.cours.weather.api.domain.port.out;

import fr.cours.weather.api.domain.model.Geocoding;

import java.util.Optional;

public interface GeocodingCache {

    Optional<Geocoding> get(String address);

    void put(String address, Geocoding geocoding);
}
