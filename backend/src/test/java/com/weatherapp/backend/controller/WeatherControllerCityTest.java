package com.weatherapp.backend.controller;

import com.weatherapp.backend.dto.WeatherResponseDto;
import com.weatherapp.backend.service.WeatherService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WeatherControllerCityTest {

    @Test
    void getWeather_debeBuscarPorCiudadCuandoNoHayCoordenadas() {
        WeatherService service = mock(WeatherService.class);
        WeatherResponseDto esperado = mock(WeatherResponseDto.class);
        when(service.getWeatherForCity("Bogotá")).thenReturn(esperado);

        WeatherResponseDto resultado = new WeatherController(service)
                .getWeather("Bogotá", null, null, null, null, null);

        assertSame(esperado, resultado);
        verify(service, never()).getWeatherForCoordinates(anyDouble(), anyDouble(), any(), any(), any());
    }

    @Test
    void getWeather_debeBuscarPorCiudadCuandoSoloLlegaLatitud() {
        WeatherService service = mock(WeatherService.class);
        WeatherResponseDto esperado = mock(WeatherResponseDto.class);
        when(service.getWeatherForCity("Lima")).thenReturn(esperado);

        WeatherResponseDto resultado = new WeatherController(service)
                .getWeather("Lima", -12.04, null, null, null, null);

        assertSame(esperado, resultado);
    }

    @Test
    void getWeather_debeBuscarPorCiudadCuandoSoloLlegaLongitud() {
        WeatherService service = mock(WeatherService.class);
        WeatherResponseDto esperado = mock(WeatherResponseDto.class);
        when(service.getWeatherForCity("Lima")).thenReturn(esperado);

        WeatherResponseDto resultado = new WeatherController(service)
                .getWeather("Lima", null, -77.03, null, null, null);

        assertSame(esperado, resultado);
    }
}
