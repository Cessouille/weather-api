package fr.cours.weather.api.infra.out.ban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.geocoding", name = "provider", havingValue = "ban")
public class BanGeocodingClient implements GeocodingClient {

    private final RestClient banApiHttpClient;

    @Override
    public Optional<Geocoding> geocode(String cityName) {
        BanSearchResponse response = banApiHttpClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("q", cityName)
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .body(BanSearchResponse.class);

        if (response == null || response.features() == null || response.features().isEmpty()) {
            log.warn("No geocoding result found for city: {}", cityName);
            return Optional.empty();
        }
        return Optional.of(toGeocoding(response.features().getFirst()));
    }

    private Geocoding toGeocoding(BanFeature feature) {
        List<Double> coordinates = feature.geometry().coordinates();
        double longitude = coordinates.get(0);
        double latitude = coordinates.get(1);
        return new Geocoding(feature.properties().label(), latitude, longitude);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record BanSearchResponse(List<BanFeature> features) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record BanFeature(BanGeometry geometry, BanProperties properties) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record BanGeometry(List<Double> coordinates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record BanProperties(String label) {
    }
}
