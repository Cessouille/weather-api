package fr.cours.weather.api.domain.port.out;

import fr.cours.weather.api.domain.model.Forecast;

public interface ForecastClient {

    Forecast getForecast(double latitude, double longitude);
}
