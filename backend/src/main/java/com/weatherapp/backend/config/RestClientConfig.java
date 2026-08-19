package com.weatherapp.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(WeatherApiProperties.class)
public class RestClientConfig {

    @Bean
    @Qualifier("geocodingRestClient")
    public RestClient geocodingRestClient(WeatherApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.geocodingUrl())
                .build();
    }

    @Bean
    @Qualifier("forecastRestClient")
    public RestClient forecastRestClient(WeatherApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.forecastUrl())
                .build();
    }
}
