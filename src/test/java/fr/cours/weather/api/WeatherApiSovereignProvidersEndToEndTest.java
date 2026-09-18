package fr.cours.weather.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Proves the sovereign providers (BAN + MET Norway) activate through
 * configuration alone, with zero code changes, per TP2 exercise 2.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                "app.geocoding.provider=ban",
                "app.weather.provider=met-norway"
        })
@AutoConfigureMockMvc
@Import(WeatherApiSovereignProvidersEndToEndTest.MockRestClientsConfig.class)
class WeatherApiSovereignProvidersEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockRestServiceServer banServer;

    @Autowired
    private MockRestServiceServer metNorwayServer;

    @AfterEach
    void resetServers() {
        banServer.reset();
        metNorwayServer.reset();
    }

    @Test
    void getForecast_usesBanAndMetNorway_whenConfiguredAsProviders() throws Exception {
        banServer.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        {"type":"FeatureCollection","features":[
                          {"type":"Feature","geometry":{"type":"Point","coordinates":[4.0817,44.1277]},
                           "properties":{"label":"Alès"}}
                        ]}
                        """, MediaType.APPLICATION_JSON));
        metNorwayServer.expect(requestTo(containsString("/compact")))
                .andRespond(withSuccess("""
                        {"type":"Feature","geometry":{"type":"Point","coordinates":[4.0817,44.1277,0.0]},
                         "properties":{"timeseries":[
                           {"time":"2026-01-01T00:00:00Z","data":{"instant":{"details":{"air_temperature":12.3}}}}
                         ]}}
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/weather/forecast").param("cityName", "Alès"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hourly[0].temperatureCelsius").value(12.3));
    }

    @TestConfiguration
    static class MockRestClientsConfig {

        @Bean
        BanStub banStub() {
            RestClient.Builder builder = RestClient.builder().baseUrl("https://api-adresse.data.gouv.fr");
            MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
            return new BanStub(server, builder.build());
        }

        @Bean
        MockRestServiceServer banServer(BanStub stub) {
            return stub.server();
        }

        @Bean("banApiHttpClient")
        RestClient banApiHttpClient(BanStub stub) {
            return stub.client();
        }

        @Bean
        MetNorwayStub metNorwayStub() {
            RestClient.Builder builder = RestClient.builder().baseUrl("https://api.met.no/weatherapi/locationforecast/2.0");
            MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
            return new MetNorwayStub(server, builder.build());
        }

        @Bean
        MockRestServiceServer metNorwayServer(MetNorwayStub stub) {
            return stub.server();
        }

        @Bean("metNorwayApiHttpClient")
        RestClient metNorwayApiHttpClient(MetNorwayStub stub) {
            return stub.client();
        }

        record BanStub(MockRestServiceServer server, RestClient client) {
        }

        record MetNorwayStub(MockRestServiceServer server, RestClient client) {
        }
    }
}
