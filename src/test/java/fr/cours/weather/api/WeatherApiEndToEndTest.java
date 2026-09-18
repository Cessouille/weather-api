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
 * Full-context test: real controller, service and client beans wired by Spring,
 * only the outbound HTTP calls to Nominatim/Open-Meteo are stubbed at the transport level.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
@Import(WeatherApiEndToEndTest.MockRestClientsConfig.class)
class WeatherApiEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockRestServiceServer nominatimServer;

    @Autowired
    private MockRestServiceServer openMeteoServer;

    @AfterEach
    void resetServers() {
        nominatimServer.reset();
        openMeteoServer.reset();
    }

    @Test
    void getForecast_returnsForecast_forKnownCity() throws Exception {
        nominatimServer.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        [{"name":"Alès","display_name":"Alès, Gard, France","lat":"44.1277","lon":"4.0817"}]
                        """, MediaType.APPLICATION_JSON));
        openMeteoServer.expect(requestTo(containsString("/forecast")))
                .andRespond(withSuccess("""
                        {"latitude":44.1277,"longitude":4.0817,"timezone":"GMT","timezone_abbreviation":"GMT",
                         "hourly":{"time":["2026-01-01T00:00"],"temperature_2m":[12.3]}}
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/weather/forecast").param("cityName", "Alès"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hourly[0].temperatureCelsius").value(12.3));
    }

    @Test
    void getForecast_returns404_whenCityIsUnknown() throws Exception {
        nominatimServer.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/weather/forecast").param("cityName", "Nowhereville"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getForecast_returns400_whenCityNameIsBlank() throws Exception {
        mockMvc.perform(get("/weather/forecast").param("cityName", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @TestConfiguration
    static class MockRestClientsConfig {

        @Bean
        NominatimStub nominatimStub() {
            RestClient.Builder builder = RestClient.builder().baseUrl("https://nominatim.openstreetmap.org");
            MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
            return new NominatimStub(server, builder.build());
        }

        @Bean
        MockRestServiceServer nominatimServer(NominatimStub stub) {
            return stub.server();
        }

        @Bean("nominatimApiHttpClient")
        RestClient nominatimApiHttpClient(NominatimStub stub) {
            return stub.client();
        }

        @Bean
        OpenMeteoStub openMeteoStub() {
            RestClient.Builder builder = RestClient.builder().baseUrl("https://api.open-meteo.com/v1");
            MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
            return new OpenMeteoStub(server, builder.build());
        }

        @Bean
        MockRestServiceServer openMeteoServer(OpenMeteoStub stub) {
            return stub.server();
        }

        @Bean("openMeteoApiHttpClient")
        RestClient openMeteoApiHttpClient(OpenMeteoStub stub) {
            return stub.client();
        }

        record NominatimStub(MockRestServiceServer server, RestClient client) {
        }

        record OpenMeteoStub(MockRestServiceServer server, RestClient client) {
        }
    }
}
