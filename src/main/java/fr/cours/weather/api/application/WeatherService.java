package fr.cours.weather.api.application;

import fr.cours.weather.api.domain.exception.CityNotFoundException;
import fr.cours.weather.api.domain.model.Forecast;
import fr.cours.weather.api.domain.model.Geocoding;
import fr.cours.weather.api.domain.port.in.WeatherUseCase;
import fr.cours.weather.api.domain.port.out.ForecastClient;
import fr.cours.weather.api.domain.port.out.GeocodingClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherService implements WeatherUseCase {

    private final GeocodingClient geocodingClient;
    private final ForecastClient forecastClient;
    private final GeocodingClient demoGeocodingClient;
    private final ForecastClient demoForecastClient;

    public WeatherService(GeocodingClient geocodingClient,
                           ForecastClient forecastClient,
                           @Qualifier("demo") GeocodingClient demoGeocodingClient,
                           @Qualifier("demo") ForecastClient demoForecastClient) {
        this.geocodingClient = geocodingClient;
        this.forecastClient = forecastClient;
        this.demoGeocodingClient = demoGeocodingClient;
        this.demoForecastClient = demoForecastClient;
    }

    @Override
    public Forecast getForecastForCity(String address, boolean demo) {
        GeocodingClient activeGeocodingClient = demo ? demoGeocodingClient : geocodingClient;
        ForecastClient activeForecastClient = demo ? demoForecastClient : forecastClient;

        Geocoding geocoding = activeGeocodingClient.geocode(address)
                .orElseThrow(() -> new CityNotFoundException(address));

        log.info("Resolved address '{}' to latitude={}, longitude={} (demo={})",
                address, geocoding.latitude(), geocoding.longitude(), demo);

        return activeForecastClient.getForecast(geocoding.latitude(), geocoding.longitude());
    }
}
