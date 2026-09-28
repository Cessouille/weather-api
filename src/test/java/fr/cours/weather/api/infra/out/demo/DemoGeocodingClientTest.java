package fr.cours.weather.api.infra.out.demo;

import fr.cours.weather.api.domain.model.Geocoding;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DemoGeocodingClientTest {

    private final DemoGeocodingClient client = new DemoGeocodingClient();

    @Test
    void geocode_returnsFixedSimulatedCoordinates_forAnyAddress() {
        Optional<Geocoding> result = client.geocode("Alès");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Alès");
        assertThat(result.get().latitude()).isEqualTo(44.1279);
        assertThat(result.get().longitude()).isEqualTo(4.0817);
    }

    @Test
    void geocode_neverReturnsEmpty_evenForAnUnknownOrNonsenseAddress() {
        Optional<Geocoding> result = client.geocode("Nowhereville");

        assertThat(result).isPresent();
    }
}
