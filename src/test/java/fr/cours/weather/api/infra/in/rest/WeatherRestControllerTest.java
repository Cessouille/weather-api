package fr.cours.weather.api.infra.in.rest;

import fr.cours.weather.api.domain.exception.CityNotFoundException;
import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.HourlyForecast;
import fr.cours.weather.api.domain.port.in.WeatherUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherRestController.class)
class WeatherRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherUseCase weatherUseCase;

    @Test
    void getForecast_returns200WithUnifiedBody_whenCityIsFound() throws Exception {
        Forecast forecast = new Forecast(48.85, 2.35, List.of(new HourlyForecast("2026-01-01T00:00", 5.4)));
        when(weatherUseCase.getForecastForCity("Paris", false)).thenReturn(forecast);

        mockMvc.perform(get("/weather/forecast").param("address", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Paris"))
                .andExpect(jsonPath("$.latitude").value(48.85))
                .andExpect(jsonPath("$.longitude").value(2.35))
                .andExpect(jsonPath("$.hourly[0].time").value("2026-01-01T00:00"))
                .andExpect(jsonPath("$.hourly[0].temperatureCelsius").value(5.4));
    }

    @Test
    void getForecast_defaultsDemoToFalse_whenDemoParamIsAbsent() throws Exception {
        Forecast forecast = new Forecast(48.85, 2.35, List.of());
        when(weatherUseCase.getForecastForCity("Paris", false)).thenReturn(forecast);

        mockMvc.perform(get("/weather/forecast").param("address", "Paris"))
                .andExpect(status().isOk());

        verify(weatherUseCase).getForecastForCity("Paris", false);
    }

    @Test
    void getForecast_passesDemoTrueToUseCase_whenDemoParamIsTrue() throws Exception {
        Forecast forecast = new Forecast(44.1279, 4.0817, List.of(new HourlyForecast("2025-06-10T14:00:00Z", 24.3)));
        when(weatherUseCase.getForecastForCity("Alès", true)).thenReturn(forecast);

        mockMvc.perform(get("/weather/forecast").param("address", "Alès").param("demo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Alès"));

        verify(weatherUseCase).getForecastForCity("Alès", true);
    }

    @Test
    void getForecast_returns404_whenCityIsNotFound() throws Exception {
        when(weatherUseCase.getForecastForCity("Nowhereville", false))
                .thenThrow(new CityNotFoundException("Nowhereville"));

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
    void getForecast_returns400_whenAddressIsBlankEvenWithDemoTrue() throws Exception {
        mockMvc.perform(get("/weather/forecast").param("address", "  ").param("demo", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getForecast_returns400_whenCityNameIsMissing() throws Exception {
        mockMvc.perform(get("/weather/forecast"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
