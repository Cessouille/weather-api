package fr.cours.weather.api.infra.out.openmeteo;

import fr.cours.weather.api.domain.model.Forecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OpenMeteoForecastClientTest {

    private MockRestServiceServer server;
    private OpenMeteoForecastClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.open-meteo.com/v1");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new OpenMeteoForecastClient(builder.build());
    }

    @Test
    void getForecast_parsesHourlyTemperatures() {
        server.expect(requestTo(containsString("/forecast")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"latitude":48.85,"longitude":2.35,"timezone":"GMT","timezone_abbreviation":"GMT",
                         "hourly":{"time":["2026-01-01T00:00"],"temperature_2m":[5.4]}}
                        """, MediaType.APPLICATION_JSON));

        Forecast forecast = client.getForecast(48.85, 2.35);

        assertThat(forecast.latitude()).isEqualTo(48.85);
        assertThat(forecast.longitude()).isEqualTo(2.35);
        assertThat(forecast.hourly().temperature()).containsExactly(5.4);
    }
}
