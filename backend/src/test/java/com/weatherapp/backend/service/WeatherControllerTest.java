
package com.weatherapp.backend.controller;

import com.weatherapp.backend.dto.WeatherResponseDto;
import com.weatherapp.backend.service.WeatherService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WeatherControllerTest {
/**
 * Verifica que el controlador consulte el clima por coordenadas
 * cuando recibe latitud y longitud.
 *
 * Patrón AAA:
 * Arrange: preparar WeatherService simulado y una respuesta.
 * Act: ejecutar getWeather enviando coordenadas.
 * Assert: comprobar que se utilizó getWeatherForCoordinates.
 */
@Test
void getWeather_debeBuscarPorCoordenadasCuandoLatYLonExisten() {

    // Arrange
    WeatherService weatherService = mock(WeatherService.class);
    WeatherResponseDto respuestaEsperada = mock(WeatherResponseDto.class);

    when(weatherService.getWeatherForCoordinates(
            6.2442,
            -75.5812,
            "Medellín",
            "Antioquia",
            "Colombia"
    )).thenReturn(respuestaEsperada);

    WeatherController controller =
            new WeatherController(weatherService);

    // Act
    WeatherResponseDto resultado = controller.getWeather(
            null,
            6.2442,
            -75.5812,
            "Medellín",
            "Antioquia",
            "Colombia"
    );

    // Assert
    assertSame(respuestaEsperada, resultado);

    verify(weatherService, times(1))
            .getWeatherForCoordinates(
                    6.2442,
                    -75.5812,
                    "Medellín",
                    "Antioquia",
                    "Colombia"
            );

    verify(weatherService, never())
            .getWeatherForCity(any());
}    
}
