package fr.cours.weather.api.domain.exception;

public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String cityName) {
        super("No location found for city: " + cityName);
    }
}
