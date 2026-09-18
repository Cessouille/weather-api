package fr.cours.weather.api.infra.out.metnorway;

import fr.cours.weather.api.domain.port.out.ForecastClient;
import fr.cours.weather.api.domain.port.out.ForecastClientContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class MetNorwayForecastClientContractTest extends ForecastClientContractTest {

    private static final String USER_AGENT = "TP2-MeteoApi/1.0 celian.cece7@gmail.com";

    private MockRestServiceServer server;
    private MetNorwayForecastClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.met.no/weatherapi/locationforecast/2.0")
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new MetNorwayForecastClient(builder.build());
    }

    @Override
    protected ForecastClient client() {
        return client;
    }

    @Override
    protected void givenForecastResponse() {
        server.expect(requestTo(containsString("/compact")))
                .andRespond(withSuccess("""
                        {"type":"Feature","geometry":{"type":"Point","coordinates":[4.0817,44.1277,0.0]},
                         "properties":{"timeseries":[
                           {"time":"2026-01-01T00:00:00Z","data":{"instant":{"details":{"air_temperature":12.3}}}}
                         ]}}
                        """, MediaType.APPLICATION_JSON));
    }

    @Override
    protected void givenEmptyForecastResponse() {
        server.expect(requestTo(containsString("/compact")))
                .andRespond(withSuccess("""
                        {"type":"Feature","geometry":{"type":"Point","coordinates":[4.0817,44.1277,0.0]},
                         "properties":{"timeseries":[]}}
                        """, MediaType.APPLICATION_JSON));
    }

    @Test
    void getForecast_sendsIdentifiableUserAgentHeader() {
        server.expect(requestTo(containsString("/compact")))
                .andExpect(header(HttpHeaders.USER_AGENT, USER_AGENT))
                .andRespond(withSuccess("""
                        {"type":"Feature","geometry":{"type":"Point","coordinates":[4.0817,44.1277,0.0]},
                         "properties":{"timeseries":[]}}
                        """, MediaType.APPLICATION_JSON));

        client.getForecast(44.1277, 4.0817);
    }
}
