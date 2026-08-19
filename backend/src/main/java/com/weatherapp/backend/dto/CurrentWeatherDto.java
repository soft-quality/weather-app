package com.weatherapp.backend.dto;

public record CurrentWeatherDto(
        double temperature,
        double apparentTemperature,
        int humidity,
        double windSpeed,
        int weatherCode,
        boolean isDay
) {
}
