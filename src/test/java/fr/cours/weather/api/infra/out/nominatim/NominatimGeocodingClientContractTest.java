package fr.cours.weather.api.infra.out.nominatim;

import fr.cours.weather.api.domain.port.out.GeocodingClient;
import fr.cours.weather.api.domain.port.out.GeocodingClientContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class NominatimGeocodingClientContractTest extends GeocodingClientContractTest {

    private MockRestServiceServer server;
    private NominatimGeocodingClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://nominatim.openstreetmap.org");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new NominatimGeocodingClient(builder.build());
    }

    @Override
    protected GeocodingClient client() {
        return client;
    }

    @Override
    protected void givenValidAddressResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        [{"name":"Alès","display_name":"Alès, Gard, France","lat":"44.1277","lon":"4.0817"}]
                        """, MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenAddressNotFoundResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenEmptyResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenAccentedAddressResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        [{"name":"Alès","display_name":"Alès, Gard, France","lat":"44.1277","lon":"4.0817"}]
                        """, MediaType.APPLICATION_JSON));
    }
}
