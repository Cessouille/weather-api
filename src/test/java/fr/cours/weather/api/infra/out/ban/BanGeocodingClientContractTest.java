package fr.cours.weather.api.infra.out.ban;

import fr.cours.weather.api.domain.port.out.GeocodingClient;
import fr.cours.weather.api.domain.port.out.GeocodingClientContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BanGeocodingClientContractTest extends GeocodingClientContractTest {

    private MockRestServiceServer server;
    private BanGeocodingClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api-adresse.data.gouv.fr");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new BanGeocodingClient(builder.build());
    }

    @Override
    protected GeocodingClient client() {
        return client;
    }

    @Override
    protected void givenValidAddressResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        {"type":"FeatureCollection","features":[
                          {"type":"Feature","geometry":{"type":"Point","coordinates":[4.0817,44.1277]},
                           "properties":{"label":"Alès","score":0.49,"city":"Alès"}}
                        ]}
                        """, MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenAddressNotFoundResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        {"type":"FeatureCollection","features":[]}
                        """, MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenEmptyResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenAccentedAddressResponse() {
        server.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        {"type":"FeatureCollection","features":[
                          {"type":"Feature","geometry":{"type":"Point","coordinates":[4.0817,44.1277]},
                           "properties":{"label":"Alès","score":0.49,"city":"Alès"}}
                        ]}
                        """, MediaType.APPLICATION_JSON));
    }
}
