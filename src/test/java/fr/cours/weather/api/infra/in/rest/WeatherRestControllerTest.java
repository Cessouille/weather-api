package fr.cours.weather.api.infra.in.rest;

import fr.cours.weather.api.domain.exception.CityNotFoundException;
import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.Hourly;
import fr.cours.weather.api.domain.port.in.WeatherUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    void getForecast_returns200WithForecastBody_whenCityIsFound() throws Exception {
        Forecast forecast = new Forecast(48.85, 2.35, "GMT", "GMT",
                new Hourly(List.of("2026-01-01T00:00"), List.of(5.4)));
        when(weatherUseCase.getForecastForCity("Paris")).thenReturn(forecast);

        mockMvc.perform(get("/weather/forecast").param("cityName", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(48.85))
                .andExpect(jsonPath("$.hourly.temperature_2m[0]").value(5.4));
    }

    @Test
    void getForecast_returns404_whenCityIsNotFound() throws Exception {
        when(weatherUseCase.getForecastForCity("Nowhereville"))
                .thenThrow(new CityNotFoundException("Nowhereville"));

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

    @Test
    void getForecast_returns400_whenCityNameIsMissing() throws Exception {
        mockMvc.perform(get("/weather/forecast"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
