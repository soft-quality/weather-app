package com.weatherapp.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(String geocodingUrl, String forecastUrl) {
}
