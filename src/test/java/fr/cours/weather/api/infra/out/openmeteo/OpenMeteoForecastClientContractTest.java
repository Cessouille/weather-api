package fr.cours.weather.api.infra.out.openmeteo;

import fr.cours.weather.api.domain.port.out.ForecastClient;
import fr.cours.weather.api.domain.port.out.ForecastClientContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OpenMeteoForecastClientContractTest extends ForecastClientContractTest {

    private MockRestServiceServer server;
    private OpenMeteoForecastClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.open-meteo.com/v1");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new OpenMeteoForecastClient(builder.build());
    }

    @Override
    protected ForecastClient client() {
        return client;
    }

    @Override
    protected void givenForecastResponse() {
        server.expect(requestTo(containsString("/forecast")))
                .andRespond(withSuccess("""
                        {"latitude":44.1277,"longitude":4.0817,"timezone":"GMT","timezone_abbreviation":"GMT",
                         "hourly":{"time":["2026-01-01T00:00"],"temperature_2m":[12.3]}}
                        """, MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenEmptyForecastResponse() {
        server.expect(requestTo(containsString("/forecast")))
                .andRespond(withSuccess("""
                        {"latitude":44.1277,"longitude":4.0817,"timezone":"GMT","timezone_abbreviation":"GMT",
                         "hourly":{"time":[],"temperature_2m":[]}}
                        """, MediaType.APPLICATION_JSON));
    }
}
