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
import static org.springframework.test.web.client.ExpectedCount.times;
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
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                "app.geocoding.provider=nominatim",
                "app.weather.provider=open-meteo"
        })
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

        mockMvc.perform(get("/weather/forecast").param("address", "Alès"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Alès"))
                .andExpect(jsonPath("$.hourly[0].temperatureCelsius").value(12.3));
    }

    @Test
    void getForecast_returns404_whenCityIsUnknown() throws Exception {
        nominatimServer.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/weather/forecast").param("address", "Nowhereville"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getForecast_returns400_whenCityNameIsBlank() throws Exception {
        mockMvc.perform(get("/weather/forecast").param("address", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getForecast_callsGeocodingOnlyOnce_forTwoSuccessiveRequestsWithSameAddress() throws Exception {
        // Uses an address not queried by any other test in this class: the geocoding
        // cache is a Spring singleton shared across test methods within a cached context,
        // so reusing "Alès" here could hit an entry another test already populated.
        nominatimServer.expect(requestTo(containsString("/search")))
                .andRespond(withSuccess("""
                        [{"name":"Nice","display_name":"Nice, Alpes-Maritimes, France","lat":"43.7102","lon":"7.2620"}]
                        """, MediaType.APPLICATION_JSON));
        openMeteoServer.expect(times(2), requestTo(containsString("/forecast")))
                .andRespond(withSuccess("""
                        {"latitude":43.7102,"longitude":7.2620,"timezone":"GMT","timezone_abbreviation":"GMT",
                         "hourly":{"time":["2026-01-01T00:00"],"temperature_2m":[18.6]}}
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/weather/forecast").param("address", "Nice"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/weather/forecast").param("address", "Nice"))
                .andExpect(status().isOk());

        nominatimServer.verify();
        openMeteoServer.verify();
    }

    @Test
    void getForecast_returnsSimulatedData_andNeverCallsExternalServices_whenDemoIsTrue() throws Exception {
        mockMvc.perform(get("/weather/forecast").param("address", "Alès").param("demo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Alès"))
                .andExpect(jsonPath("$.latitude").value(44.1279))
                .andExpect(jsonPath("$.longitude").value(4.0817))
                .andExpect(jsonPath("$.hourly[0].time").value("2025-06-10T14:00:00Z"))
                .andExpect(jsonPath("$.hourly[0].temperatureCelsius").value(24.3));

        nominatimServer.verify();
        openMeteoServer.verify();
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
