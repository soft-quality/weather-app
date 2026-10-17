package com.weatherapp.backend.controller;

import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.dto.CurrentWeatherDto;
import com.weatherapp.backend.dto.DailyForecastDto;
import com.weatherapp.backend.dto.LocationDto;
import com.weatherapp.backend.dto.WeatherResponseDto;
import com.weatherapp.backend.exception.CityNotFoundException;
import com.weatherapp.backend.exception.GlobalExceptionHandler;
import com.weatherapp.backend.exception.WeatherProviderException;
import com.weatherapp.backend.service.GeocodingService;
import com.weatherapp.backend.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas de la capa HTTP (controladores + manejador global de errores) sin levantar el contexto completo. */
class ControllersMockMvcTest {

    private WeatherService weatherService;
    private GeocodingService geocodingService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        weatherService = mock(WeatherService.class);
        geocodingService = mock(GeocodingService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new WeatherController(weatherService), new CityController(geocodingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private WeatherResponseDto sampleWeather() {
        return new WeatherResponseDto(
                new LocationDto("Medellín", "Antioquia", "Colombia", 6.24, -75.58),
                new CurrentWeatherDto(22.5, 23.0, 60, 10.0, 3, true),
                List.of(new DailyForecastDto("2026-01-01", 25.0, 15.0, 1)));
    }

    @Test
    void getWeather_porCiudad_retorna200ConElClima() throws Exception {
        when(weatherService.getWeatherForCity("Medellín")).thenReturn(sampleWeather());

        mockMvc.perform(get("/api/weather").param("city", "Medellín"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.name").value("Medellín"))
                .andExpect(jsonPath("$.current.temperature").value(22.5))
                .andExpect(jsonPath("$.daily[0].date").value("2026-01-01"));
    }

    @Test
    void getWeather_porCoordenadas_retorna200() throws Exception {
        when(weatherService.getWeatherForCoordinates(6.24, -75.58, "Medellín", "Antioquia", "Colombia"))
                .thenReturn(sampleWeather());

        mockMvc.perform(get("/api/weather")
                        .param("lat", "6.24").param("lon", "-75.58")
                        .param("name", "Medellín").param("admin1", "Antioquia").param("country", "Colombia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.country").value("Colombia"));
    }

    @Test
    void getWeather_ciudadInexistente_retorna404() throws Exception {
        when(weatherService.getWeatherForCity("Xyz")).thenThrow(new CityNotFoundException("Xyz"));

        mockMvc.perform(get("/api/weather").param("city", "Xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró la ciudad: Xyz"));
    }

    @Test
    void getWeather_falloDelProveedor_retorna502() throws Exception {
        when(weatherService.getWeatherForCity("Cali")).thenThrow(new WeatherProviderException("proveedor caído"));

        mockMvc.perform(get("/api/weather").param("city", "Cali"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value("proveedor caído"));
    }

    @Test
    void searchCities_retorna200ConSugerencias() throws Exception {
        when(geocodingService.searchCities("Med")).thenReturn(List.of(
                new GeocodingResult("Medellín", 6.24, -75.58, "Colombia", "Antioquia")));

        mockMvc.perform(get("/api/cities").param("query", "Med"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Medellín"))
                .andExpect(jsonPath("$[0].admin1").value("Antioquia"));
    }

    @Test
    void searchCities_sinQuery_retornaListaVacia() throws Exception {
        when(geocodingService.searchCities(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
