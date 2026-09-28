package fr.cours.weather.api.infra.out.cache;

import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.out.GeocodingCache;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@Primary
public class CachingGeocodingClient implements GeocodingClient {

    private final GeocodingClient delegate;
    private final GeocodingCache cache;

    public CachingGeocodingClient(@Qualifier("live") GeocodingClient delegate, GeocodingCache cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Optional<Geocoding> geocode(String address) {
        Optional<Geocoding> cached = cache.get(address);
        if (cached.isPresent()) {
            log.debug("Geocoding cache hit for address: {}", address);
            return cached;
        }

        Optional<Geocoding> result = delegate.geocode(address);
        result.ifPresent(geocoding -> cache.put(address, geocoding));
        return result;
    }
}
