package fr.cours.weather.api.infra.out.nominatim;

import fr.cours.weather.api.domain.model.Geocoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class NominatimGeocodingClientTest {

    private MockRestServiceServer server;
    private NominatimGeocodingClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://nominatim.openstreetmap.org");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new NominatimGeocodingClient(builder.build());
    }

    @Test
    void geocode_returnsFirstResult_whenNominatimRespondsWithMatches() {
        server.expect(requestTo(containsString("/search")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"name":"Alès","display_name":"Alès, Gard, France","lat":"44.1277","lon":"4.0817"}]
                        """, MediaType.APPLICATION_JSON));

        Optional<Geocoding> result = client.geocode("Alès");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Alès");
        assertThat(result.get().latitude()).isEqualTo("44.1277");
        assertThat(result.get().longitude()).isEqualTo("4.0817");
    }

    @Test
    void geocode_returnsEmpty_whenNominatimRespondsWithNoMatches() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        Optional<Geocoding> result = client.geocode("Nowhereville");

        assertThat(result).isEmpty();
    }
}
