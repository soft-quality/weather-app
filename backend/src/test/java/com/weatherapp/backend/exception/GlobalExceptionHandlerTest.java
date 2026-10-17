package com.weatherapp.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleCityNotFound_debeRetornar404() {
        ResponseEntity<ApiError> response = handler.handleCityNotFound(new CityNotFoundException("Atlantis"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("No se encontró la ciudad: Atlantis", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleWeatherProvider_debeRetornar502() {
        ResponseEntity<ApiError> response = handler.handleWeatherProvider(new WeatherProviderException("fallo"));

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals(502, response.getBody().status());
        assertEquals("fallo", response.getBody().message());
    }

    @Test
    void handleMissingParameter_debeRetornar400() {
        ResponseEntity<ApiError> response =
                handler.handleMissingParameter(new MissingServletRequestParameterException("city", "String"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("El parámetro 'city' es obligatorio", response.getBody().message());
    }
}
