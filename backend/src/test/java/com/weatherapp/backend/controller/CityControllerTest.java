package com.weatherapp.backend.controller;

import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.dto.LocationDto;
import com.weatherapp.backend.service.GeocodingService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CityControllerTest {

    @Test
    void searchCities_debeMapearResultadosALocationDto() {
        // Arrange
        GeocodingService service = mock(GeocodingService.class);
        when(service.searchCities("Med")).thenReturn(List.of(
                new GeocodingResult("Medellín", 6.2442, -75.5812, "Colombia", "Antioquia"),
                new GeocodingResult("Medina", 24.47, 39.61, "Arabia Saudita", null)));
        CityController controller = new CityController(service);

        // Act
        List<LocationDto> resultado = controller.searchCities("Med");

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(new LocationDto("Medellín", "Antioquia", "Colombia", 6.2442, -75.5812), resultado.get(0));
        assertEquals("Medina", resultado.get(1).name());
        verify(service).searchCities("Med");
    }

    @Test
    void searchCities_debeRetornarListaVaciaCuandoNoHayResultados() {
        GeocodingService service = mock(GeocodingService.class);
        when(service.searchCities(null)).thenReturn(List.of());

        List<LocationDto> resultado = new CityController(service).searchCities(null);

        assertTrue(resultado.isEmpty());
    }
}
