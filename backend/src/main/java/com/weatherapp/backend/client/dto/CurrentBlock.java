package com.weatherapp.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentBlock(
        @JsonProperty("temperature_2m") double temperature,
        @JsonProperty("apparent_temperature") double apparentTemperature,
        @JsonProperty("relative_humidity_2m") int humidity,
        @JsonProperty("wind_speed_10m") double windSpeed,
        @JsonProperty("weather_code") int weatherCode,
        @JsonProperty("is_day") int isDay
) {
}
