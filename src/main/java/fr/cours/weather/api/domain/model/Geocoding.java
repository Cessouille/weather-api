package fr.cours.weather.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Geocoding(

        String name,

        @JsonProperty("display_name")
        String displayName,

        @JsonProperty("lat")
        String latitude,

        @JsonProperty("lon")
        String longitude
) {
}
