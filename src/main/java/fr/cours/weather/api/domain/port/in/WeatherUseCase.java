package fr.cours.weather.api.domain.port.in;

import fr.cours.weather.api.domain.model.Forecast;

public interface WeatherUseCase {

    Forecast getForecastForCity(String cityName);
}
