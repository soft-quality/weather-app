package com.weatherapp.backend.service;
import java.util.List;

import com.weatherapp.backend.client.dto.CurrentBlock;
import com.weatherapp.backend.client.dto.DailyBlock;
import com.weatherapp.backend.client.dto.ForecastApiResponse;
import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.dto.WeatherResponseDto;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import com.weatherapp.backend.exception.WeatherProviderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherServiceTest {

    /**
     * Verifica que WeatherService obtenga correctamente el clima
     * de una ciudad cuando GeocodingService y la API del clima
     * responden correctamente.
     *
     * Patrón AAA:
     * Arrange: preparar ciudad, clima y mocks.
     * Act: solicitar el clima de Medellín.
     * Assert: verificar ubicación, temperatura y pronóstico.
     */
    @Test
    void getWeatherForCity_debeRetornarClimaCorrectamente() {

        // Arrange

        RestClient restClient = mock(RestClient.class);
        GeocodingService geocodingService = mock(GeocodingService.class);

        RequestHeadersUriSpec requestSpec =
                mock(RequestHeadersUriSpec.class);

        ResponseSpec responseSpec =
                mock(ResponseSpec.class);

        GeocodingResult medellin = new GeocodingResult(
                "Medellín",
                6.2442,
                -75.5812,
                "Colombia",
                "Antioquia"
        );

        when(geocodingService.findCity("Medellin"))
                .thenReturn(medellin);

        CurrentBlock current = new CurrentBlock(
                25.0,
                27.0,
                70,
                10.0,
                3,
                1
        );

        DailyBlock daily = new DailyBlock(
                List.of("2026-08-16"),
                List.of(30.0),
                List.of(20.0),
                List.of(3)
        );

        ForecastApiResponse respuestaClima =
                new ForecastApiResponse(current, daily);

        when(restClient.get()).thenReturn(requestSpec);

        when(requestSpec.uri(any(java.util.function.Function.class)))
                .thenReturn(requestSpec);

        when(requestSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.body(ForecastApiResponse.class))
                .thenReturn(respuestaClima);

        WeatherService service =
                new WeatherService(restClient, geocodingService);

        // Act

        WeatherResponseDto resultado =
                service.getWeatherForCity("Medellin");

        // Assert

        assertEquals("Medellín", resultado.location().name());
        assertEquals("Colombia", resultado.location().country());
        assertEquals(25.0, resultado.current().temperature());
        assertEquals(1, resultado.daily().size());
    }
// Prueba unitaria para Validacion

/**if (forecast == null || forecast.current() == null || forecast.daily() == null) {
    throw new WeatherProviderException(
            "No se pudo obtener el clima para " + label
    );
} */
/**
 * Verifica que WeatherService lance WeatherProviderException
 * cuando la API del clima no devuelve ninguna respuesta.
 *
 * Patrón AAA:
 * Arrange: preparar la ciudad y simular que la API devuelve null.
 * Act: solicitar el clima de Medellín.
 * Assert: comprobar que se lance WeatherProviderException.
 */
@Test
void getWeatherForCity_debeLanzarExcepcionCuandoApiClimaRetornaNull() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    GeocodingService geocodingService = mock(GeocodingService.class);

    RequestHeadersUriSpec requestSpec =
            mock(RequestHeadersUriSpec.class);

    ResponseSpec responseSpec =
            mock(ResponseSpec.class);

    GeocodingResult medellin = new GeocodingResult(
            "Medellín",
            6.2442,
            -75.5812,
            "Colombia",
            "Antioquia"
    );

    when(geocodingService.findCity("Medellin"))
            .thenReturn(medellin);

    when(restClient.get())
            .thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve())
            .thenReturn(responseSpec);

    when(responseSpec.body(ForecastApiResponse.class))
            .thenReturn(null);

    WeatherService service =
            new WeatherService(restClient, geocodingService);

    // Act + Assert
    assertThrows(
            WeatherProviderException.class,
            () -> service.getWeatherForCity("Medellin")
    );
}
// Prueba unitaria para Validacion

/**if (forecast == null || forecast.current() == null || forecast.daily() == null) {
    throw new WeatherProviderException(...);
} */

    /**
 * Verifica que WeatherService lance WeatherProviderException
 * cuando la respuesta existe, pero no contiene información
 * del clima actual.
 *
 * Patrón AAA:
 * Arrange: simular una respuesta con current = null.
 * Act: solicitar el clima de Medellín.
 * Assert: comprobar que se lance WeatherProviderException.
 */
@Test
void getWeatherForCity_debeLanzarExcepcionCuandoCurrentEsNull() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    GeocodingService geocodingService = mock(GeocodingService.class);

    RequestHeadersUriSpec requestSpec =
            mock(RequestHeadersUriSpec.class);

    ResponseSpec responseSpec =
            mock(ResponseSpec.class);

    GeocodingResult medellin = new GeocodingResult(
            "Medellín",
            6.2442,
            -75.5812,
            "Colombia",
            "Antioquia"
    );

    when(geocodingService.findCity("Medellin"))
            .thenReturn(medellin);

    DailyBlock daily = new DailyBlock(
            List.of("2026-08-16"),
            List.of(30.0),
            List.of(20.0),
            List.of(3)
    );

    ForecastApiResponse respuestaClima =
            new ForecastApiResponse(null, daily);

    when(restClient.get()).thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.body(ForecastApiResponse.class))
            .thenReturn(respuestaClima);

    WeatherService service =
            new WeatherService(restClient, geocodingService);

    // Act + Assert
    assertThrows(
            WeatherProviderException.class,
            () -> service.getWeatherForCity("Medellin")
    );
}
// PRUEBA UNITARIA PARA VALIDACION

/**if (forecast == null || forecast.current() == null || forecast.daily() == null) {
    throw new WeatherProviderException(...);
} */


/**
 * Verifica que WeatherService lance WeatherProviderException
 * cuando la respuesta existe, pero no contiene el pronóstico diario.
 *
 * Patrón AAA:
 * Arrange: simular una respuesta con daily = null.
 * Act: solicitar el clima de Medellín.
 * Assert: comprobar que se lance WeatherProviderException.
 */
@Test
void getWeatherForCity_debeLanzarExcepcionCuandoDailyEsNull() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    GeocodingService geocodingService = mock(GeocodingService.class);

    RequestHeadersUriSpec requestSpec =
            mock(RequestHeadersUriSpec.class);

    ResponseSpec responseSpec =
            mock(ResponseSpec.class);

    GeocodingResult medellin = new GeocodingResult(
            "Medellín",
            6.2442,
            -75.5812,
            "Colombia",
            "Antioquia"
    );

    when(geocodingService.findCity("Medellin"))
            .thenReturn(medellin);

    CurrentBlock current = new CurrentBlock(
            25.0,
            27.0,
            70,
            10.0,
            3,
            1
    );

    ForecastApiResponse respuestaClima =
            new ForecastApiResponse(current, null);

    when(restClient.get()).thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.body(ForecastApiResponse.class))
            .thenReturn(respuestaClima);

    WeatherService service =
            new WeatherService(restClient, geocodingService);

    // Act + Assert
    assertThrows(
            WeatherProviderException.class,
            () -> service.getWeatherForCity("Medellin")
    );
}
/**
 * Verifica que WeatherService retorne correctamente el clima
 * cuando se proporcionan directamente las coordenadas.
 *
 * Patrón AAA:
 * Arrange: preparar coordenadas, ubicación y respuesta simulada.
 * Act: solicitar el clima por coordenadas.
 * Assert: comprobar ubicación, temperatura y pronóstico.
 */
@Test
void getWeatherForCoordinates_debeRetornarClimaCorrectamente() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    GeocodingService geocodingService = mock(GeocodingService.class);

    RequestHeadersUriSpec requestSpec =
            mock(RequestHeadersUriSpec.class);

    ResponseSpec responseSpec =
            mock(ResponseSpec.class);

    CurrentBlock current = new CurrentBlock(
            26.0,
            28.0,
            75,
            12.0,
            2,
            1
    );

    DailyBlock daily = new DailyBlock(
            List.of("2026-08-16"),
            List.of(31.0),
            List.of(21.0),
            List.of(2)
    );

    ForecastApiResponse respuestaClima =
            new ForecastApiResponse(current, daily);

    when(restClient.get()).thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.body(ForecastApiResponse.class))
            .thenReturn(respuestaClima);

    WeatherService service =
            new WeatherService(restClient, geocodingService);

    // Act
    WeatherResponseDto resultado =
            service.getWeatherForCoordinates(
                    6.2442,
                    -75.5812,
                    "Medellín",
                    "Antioquia",
                    "Colombia"
            );

    // Assert
    assertEquals("Medellín", resultado.location().name());
    assertEquals(6.2442, resultado.location().latitude());
    assertEquals(-75.5812, resultado.location().longitude());
    assertEquals(26.0, resultado.current().temperature());
    assertEquals(1, resultado.daily().size());
}

}