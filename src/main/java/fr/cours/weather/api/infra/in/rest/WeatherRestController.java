package fr.cours.weather.api.infra.in.rest;

import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.port.in.WeatherUseCase;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherRestController {

    private final WeatherUseCase weatherUseCase;

    @GetMapping("/forecast")
    public Forecast getForecast(
            @RequestParam
            @NotBlank(message = "cityName must not be blank")
            String cityName
    ) {
        log.info("REST request to get forecast for city: {}", cityName);
        return weatherUseCase.getForecastForCity(cityName);
    }
}
