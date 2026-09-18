package fr.cours.weather.api.infra.out.nominatim;

import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NominatimGeocodingClient implements GeocodingClient {

    private final RestClient nominatimApiHttpClient;

    @Override
    public Optional<Geocoding> geocode(String cityName) {
        List<Geocoding> results = nominatimApiHttpClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("q", cityName)
                        .queryParam("format", "json")
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Geocoding>>() {
                });

        if (results == null || results.isEmpty()) {
            log.warn("No geocoding result found for city: {}", cityName);
            return Optional.empty();
        }
        return Optional.of(results.getFirst());
    }
}
