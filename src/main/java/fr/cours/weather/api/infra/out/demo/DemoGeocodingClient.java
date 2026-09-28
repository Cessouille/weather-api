package fr.cours.weather.api.infra.out.demo;

import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Qualifier("demo")
public class DemoGeocodingClient implements GeocodingClient {

    private static final double SIMULATED_LATITUDE = 44.1279;
    private static final double SIMULATED_LONGITUDE = 4.0817;

    @Override
    public Optional<Geocoding> geocode(String address) {
        return Optional.of(new Geocoding(address, SIMULATED_LATITUDE, SIMULATED_LONGITUDE));
    }
}
