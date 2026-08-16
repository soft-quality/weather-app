package com.weatherapp.backend.service;

import com.weatherapp.backend.client.dto.GeocodingApiResponse;
import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.exception.CityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    private final RestClient geocodingRestClient;

    public GeocodingService(@Qualifier("geocodingRestClient") RestClient geocodingRestClient) {
        this.geocodingRestClient = geocodingRestClient;
    }

    public GeocodingResult findCity(String cityName) {
        logger.info("Buscando coordenadas para la ciudad '{}'", cityName);

        GeocodingApiResponse response = geocodingRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("name", cityName)
                        .queryParam("count", 1)
                        .queryParam("language", "es")
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .body(GeocodingApiResponse.class);

        if (response == null || response.results() == null || response.results().isEmpty()) {
            throw new CityNotFoundException(cityName);
        }

        return response.results().get(0);
    }
}
