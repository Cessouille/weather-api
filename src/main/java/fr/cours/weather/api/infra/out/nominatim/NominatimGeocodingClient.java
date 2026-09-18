package fr.cours.weather.api.infra.out.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.geocoding", name = "provider", havingValue = "nominatim", matchIfMissing = true)
public class NominatimGeocodingClient implements GeocodingClient {

    private final RestClient nominatimApiHttpClient;

    @Override
    public Optional<Geocoding> geocode(String cityName) {
        List<NominatimResult> results = nominatimApiHttpClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("q", cityName)
                        .queryParam("format", "json")
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<NominatimResult>>() {
                });

        if (results == null || results.isEmpty()) {
            log.warn("No geocoding result found for city: {}", cityName);
            return Optional.empty();
        }
        return Optional.of(toGeocoding(results.getFirst()));
    }

    private Geocoding toGeocoding(NominatimResult result) {
        return new Geocoding(result.name(), Double.parseDouble(result.latitude()), Double.parseDouble(result.longitude()));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NominatimResult(
            String name,
            @JsonProperty("lat") String latitude,
            @JsonProperty("lon") String longitude
    ) {
    }
}
