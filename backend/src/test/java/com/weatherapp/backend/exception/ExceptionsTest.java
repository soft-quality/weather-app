package com.weatherapp.backend.exception;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ExceptionsTest {

    @Test
    void cityNotFoundException_debeIncluirNombreDeLaCiudadEnElMensaje() {
        CityNotFoundException ex = new CityNotFoundException("Bogotá");

        assertEquals("No se encontró la ciudad: Bogotá", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void weatherProviderException_debeConservarElMensaje() {
        WeatherProviderException ex = new WeatherProviderException("error");

        assertEquals("error", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void apiError_debeExponerSusCampos() {
        Instant now = Instant.now();
        ApiError error = new ApiError(now, 500, "boom");

        assertEquals(now, error.timestamp());
        assertEquals(500, error.status());
        assertEquals("boom", error.message());
        assertEquals(new ApiError(now, 500, "boom"), error);
    }
}
