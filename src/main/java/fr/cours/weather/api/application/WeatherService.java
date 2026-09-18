package fr.cours.weather.api.application;

import fr.cours.weather.api.domain.exception.CityNotFoundException;
import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.in.WeatherUseCase;
import fr.cours.weather.api.domain.port.out.ForecastClient;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService implements WeatherUseCase {

    private final GeocodingClient geocodingClient;
    private final ForecastClient forecastClient;

    @Override
    public Forecast getForecastForCity(String cityName) {
        Geocoding geocoding = geocodingClient.geocode(cityName)
                .orElseThrow(() -> new CityNotFoundException(cityName));

        double latitude = Double.parseDouble(geocoding.latitude());
        double longitude = Double.parseDouble(geocoding.longitude());
        log.info("Resolved city '{}' to latitude={}, longitude={}", cityName, latitude, longitude);

        return forecastClient.getForecast(latitude, longitude);
    }
}
