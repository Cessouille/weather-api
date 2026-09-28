package fr.cours.weather.api.infra.out.cache;

import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.out.GeocodingCache;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryGeocodingCache implements GeocodingCache {

    private final Map<String, Geocoding> entries = new ConcurrentHashMap<>();

    @Override
    public Optional<Geocoding> get(String address) {
        return Optional.ofNullable(entries.get(address));
    }

    @Override
    public void put(String address, Geocoding geocoding) {
        entries.put(address, geocoding);
    }
}
