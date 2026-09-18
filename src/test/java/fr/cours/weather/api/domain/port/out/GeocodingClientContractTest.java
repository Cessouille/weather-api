package fr.cours.weather.api.domain.port.out;

import fr.cours.weather.api.domain.model.Geocoding;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contract every GeocodingClient implementation must satisfy. Each adapter's
 * test subclass stubs its own HTTP responses via the given* hooks below.
 */
public abstract class GeocodingClientContractTest {

    protected abstract GeocodingClient client();

    protected abstract void givenValidAddressResponse();

    protected abstract void givenAddressNotFoundResponse();

    protected abstract void givenEmptyResponse();

    protected abstract void givenAccentedAddressResponse();

    @Test
    void geocode_returnsCoordinates_whenAddressIsValid() {
        givenValidAddressResponse();

        Optional<Geocoding> result = client().geocode("Alès");

        assertThat(result).isPresent();
        assertThat(result.get().latitude()).isCloseTo(44.1277, Offset.offset(0.01));
        assertThat(result.get().longitude()).isCloseTo(4.0817, Offset.offset(0.01));
    }

    @Test
    void geocode_returnsEmpty_whenAddressIsNotFound() {
        givenAddressNotFoundResponse();

        Optional<Geocoding> result = client().geocode("Nowhereville");

        assertThat(result).isEmpty();
    }

    @Test
    void geocode_returnsEmpty_whenResponseIsEmpty() {
        givenEmptyResponse();

        Optional<Geocoding> result = client().geocode("Nowhereville");

        assertThat(result).isEmpty();
    }

    @Test
    void geocode_handlesAccentedCharacters() {
        givenAccentedAddressResponse();

        Optional<Geocoding> result = client().geocode("Alès");

        assertThat(result).isPresent();
        assertThat(result.get().name()).contains("è");
    }
}
