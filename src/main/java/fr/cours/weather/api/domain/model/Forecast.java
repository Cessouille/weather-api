package fr.cours.weather.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Forecast(

        Double latitude,

        Double longitude,

        String timezone,

        @JsonProperty("timezone_abbreviation")
        String timezoneAbbreviation,

        Hourly hourly
) {
}
