package fr.cours.weather.api.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Hourly(

        List<String> time,

        @JsonProperty("temperature_2m")
        List<Double> temperature
) {
}
