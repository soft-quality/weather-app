package com.weatherapp.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


import com.weatherapp.backend.client.dto.GeocodingApiResponse;
import com.weatherapp.backend.client.dto.GeocodingResult;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import com.weatherapp.backend.exception.CityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;



/**
 * ============================================================
 *  PRUEBAS UNITARIAS — GeocodingService
 * ============================================================
 *
 * PROPÓSITO
 * ---------
 * Estas pruebas verifican que GeocodingService funcione
 * correctamente de manera aislada.
 *
 * Se validan escenarios como:
 *   1. Consultas null, vacías o con espacios.
 *   2. Búsquedas válidas de ciudades.
 *   3. Ciudades no encontradas.
 *   4. Respuestas null de la API externa.
 *   5. Correcto uso de RestClient.
 *
 * PATRÓN APLICADO: AAA (Arrange – Act – Assert)
 * ----------------------------------------------
 * Cada prueba está organizada en tres etapas:
 *
 *  - Arrange : preparar datos, objetos y mocks necesarios.
 *  - Act     : ejecutar el método que se desea probar.
 *  - Assert  : verificar que el resultado sea el esperado.
 *
 * EJEMPLO
 * -------
 * Para comprobar una consulta null:
 *
 *  Arrange:
 *      String query = null;
 *
 *  Act:
 *      var resultado = service.searchCities(query);
 *
 *  Assert:
 *      assertTrue(resultado.isEmpty());
 *
 * En este caso se espera una lista vacía porque el método
 * searchCities valida primero si la consulta es null o blank.
 *
 * POR QUÉ SE USAN MOCKS
 * ----------------------
 * GeocodingService utiliza RestClient para consultar una API
 * externa. En las pruebas unitarias no queremos depender de
 * internet ni realizar peticiones reales.
 *
 * Mockito permite simular esa dependencia, por ejemplo:
 *
 *      when(restClient.get()).thenReturn(requestSpec);
 *
 * Así se prueba únicamente la lógica de GeocodingService
 * utilizando respuestas controladas y reproducibles.
 *
 * HERRAMIENTAS
 * ------------
 * - JUnit 5 : ejecución y validación de las pruebas.
 * - Mockito : creación de mocks y simulación de dependencias.
 * - Gradle  : compilación y ejecución de las pruebas.
 *
 * EJECUCIÓN:
 *      ./gradlew test
 *
 * En Windows:
 *      .\gradlew.bat test
 */




public class GeocodingServiceTest {
    //PRiMER METODO A EVALUAR

    /**if (query == null || query.isBlank()) {
    return List.of();
    }*/

    /**
 * Verifica que searchCities retorne una lista vacía
 * cuando el texto de búsqueda recibido sea null.
 *
 * Se utiliza el patrón AAA:
 * Arrange: preparar el servicio y el valor de entrada.
 * Act: ejecutar searchCities.
 * Assert: comprobar que el resultado esté vacío.
 */
@Test
void searchCities_debeRetornarListaVaciaCuandoQueryEsNull() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    GeocodingService service = new GeocodingService(restClient);
    String query = null;

    // Act
    var resultado = service.searchCities(query);

    // Assert
    assertTrue(resultado.isEmpty());

    /**
 * Verifica que searchCities retorne una lista vacía
 * cuando el texto de búsqueda esté vacío.
 *
 * Patrón AAA:
 * Arrange: preparar el servicio y una consulta vacía.
 * Act: ejecutar searchCities.
 * Assert: comprobar que el resultado esté vacío.
 */

}

@Test
void searchCities_debeRetornarListaVaciaCuandoQueryEstaVacia() {

    // Arrange : clase real tienen un contructor por eso necesita un Restclient.
    RestClient restClient = mock(RestClient.class);
    GeocodingService service = new GeocodingService(restClient);
    String query = "";

    // Act
    var resultado = service.searchCities(query);

    // Assert
    assertTrue(resultado.isEmpty());
}
/**
 * Verifica que searchCities retorne una lista vacía
 * cuando la consulta contiene únicamente espacios.
 *
 * Patrón AAA:
 * Arrange: preparar el servicio y una consulta con espacios.
 * Act: ejecutar searchCities.
 * Assert: comprobar que el resultado esté vacío.
 */
@Test
void searchCities_debeRetornarListaVaciaCuandoQuerySoloTieneEspacios() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    GeocodingService service = new GeocodingService(restClient);
    String query = "   ";

    // Act
    var resultado = service.searchCities(query);

    // Assert
    assertTrue(resultado.isEmpty());
}

// SEGUNDO METODO A EVALUAR

/** GeocodingApiResponse response = geocodingRestClient.get()
        .uri(...)
        .retrieve()
        .body(GeocodingApiResponse.class);*/
/**
 * Verifica que searchCities retorne correctamente las ciudades
 * recibidas desde el servicio externo.
 *
 * La API externa se simula utilizando Mockito,
 * evitando realizar una petición real por Internet.
 *
 * Patrón AAA:
 * Arrange: preparar los mocks y una respuesta simulada.
 * Act: ejecutar searchCities con una ciudad válida.
 * Assert: comprobar que el resultado sea el esperado.
 */
@Test
void searchCities_debeRetornarCiudadesCuandoQueryEsValida() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    RequestHeadersUriSpec requestSpec = mock(RequestHeadersUriSpec.class);
    ResponseSpec responseSpec = mock(ResponseSpec.class);

    GeocodingResult medellin = new GeocodingResult(
            "Medellín",
            6.2442,
            -75.5812,
            "Colombia",
            "Antioquia"
    );

    GeocodingApiResponse respuestaApi =
            new GeocodingApiResponse(List.of(medellin));

    when(restClient.get()).thenReturn(requestSpec);
    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);
    when(requestSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(GeocodingApiResponse.class))
            .thenReturn(respuestaApi);

    GeocodingService service = new GeocodingService(restClient);

    // Act
    var resultado = service.searchCities("Medellin");

    // Assert
    assertEquals(1, resultado.size());
    assertEquals("Medellín", resultado.get(0).name());
    assertEquals("Colombia", resultado.get(0).country());
}
// 3 METODO DE PRUEBA findCity()
/**if (response == null || response.results() == null || response.results().isEmpty()) {
    throw new CityNotFoundException(cityName);
} */

/**
 * Verifica que findCity lance una excepción cuando
 * la API no encuentre resultados para la ciudad buscada.
 *
 * Patrón AAA:
 * Arrange: simular una respuesta de la API sin resultados.
 * Act: ejecutar findCity con una ciudad inexistente.
 * Assert: comprobar que se lance CityNotFoundException.
 */
@Test
void findCity_debeLanzarExcepcionCuandoCiudadNoExiste() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    RequestHeadersUriSpec requestSpec = mock(RequestHeadersUriSpec.class);
    ResponseSpec responseSpec = mock(ResponseSpec.class);

    GeocodingApiResponse respuestaApi =
            new GeocodingApiResponse(List.of());

    when(restClient.get()).thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.body(GeocodingApiResponse.class))
            .thenReturn(respuestaApi);

    GeocodingService service = new GeocodingService(restClient);

    // Act + Assert
    assertThrows(
            CityNotFoundException.class,
            () -> service.findCity("CiudadInventada")
    );
}
/**
 * Verifica que findCity retorne correctamente la primera ciudad
 * encontrada cuando la API devuelve resultados.
 *
 * Patrón AAA:
 * Arrange: simular una respuesta de la API con una ciudad.
 * Act: ejecutar findCity.
 * Assert: comprobar que la ciudad retornada sea la esperada.
 */
@Test
void findCity_debeRetornarCiudadCuandoExiste() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    RequestHeadersUriSpec requestSpec = mock(RequestHeadersUriSpec.class);
    ResponseSpec responseSpec = mock(ResponseSpec.class);

    GeocodingResult medellin = new GeocodingResult(
            "Medellín",
            6.2442,
            -75.5812,
            "Colombia",
            "Antioquia"
    );

    GeocodingApiResponse respuestaApi =
            new GeocodingApiResponse(List.of(medellin));

    when(restClient.get()).thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.body(GeocodingApiResponse.class))
            .thenReturn(respuestaApi);

    GeocodingService service = new GeocodingService(restClient);

    // Act
    GeocodingResult resultado = service.findCity("Medellin");

    // Assert
    assertEquals("Medellín", resultado.name());
    assertEquals("Colombia", resultado.country());
    assertEquals("Antioquia", resultado.admin1());
}
// 4 METODO searchCities()
/**if (response == null || response.results() == null) {
    return List.of();
}*/

/**
 * Verifica que searchCities retorne una lista vacía
 * cuando la API externa no devuelve una respuesta.
 *
 * Patrón AAA:
 * Arrange: simular que la API devuelve null.
 * Act: ejecutar searchCities con una consulta válida.
 * Assert: comprobar que el resultado sea una lista vacía.
 */
@Test
void searchCities_debeRetornarListaVaciaCuandoRespuestaApiEsNull() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    RequestHeadersUriSpec requestSpec = mock(RequestHeadersUriSpec.class);
    ResponseSpec responseSpec = mock(ResponseSpec.class);

    when(restClient.get()).thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.body(GeocodingApiResponse.class))
            .thenReturn(null);

    GeocodingService service = new GeocodingService(restClient);

    // Act
    var resultado = service.searchCities("Medellin");

    // Assert
    assertTrue(resultado.isEmpty());
}
/**
 * Verifica que searchCities no realice una llamada a la API
 * cuando la consulta está vacía.
 *
 * Patrón AAA:
 * Arrange: preparar un RestClient simulado y una consulta vacía.
 * Act: ejecutar searchCities.
 * Assert: comprobar que RestClient.get() nunca haya sido llamado.
 */
@Test
void searchCities_noDebeConsultarApiCuandoQueryEstaVacia() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    GeocodingService service = new GeocodingService(restClient);
    String query = "";

    // Act
    service.searchCities(query);

    // Assert
    verify(restClient, never()).get();
}
/**
 * Verifica que searchCities consulte la API exactamente una vez
 * cuando recibe una consulta válida.
 *
 * Patrón AAA:
 * Arrange: preparar los mocks y una respuesta simulada.
 * Act: ejecutar searchCities con una ciudad válida.
 * Assert: comprobar que RestClient.get() se llamó una vez.
 */
@Test
void searchCities_debeConsultarApiUnaVezCuandoQueryEsValida() {

    // Arrange
    RestClient restClient = mock(RestClient.class);
    RequestHeadersUriSpec requestSpec = mock(RequestHeadersUriSpec.class);
    ResponseSpec responseSpec = mock(ResponseSpec.class);

    GeocodingApiResponse respuestaApi =
            new GeocodingApiResponse(List.of());

    when(restClient.get()).thenReturn(requestSpec);

    when(requestSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(requestSpec);

    when(requestSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.body(GeocodingApiResponse.class))
            .thenReturn(respuestaApi);

    GeocodingService service = new GeocodingService(restClient);

    // Act
    service.searchCities("Medellin");

    // Assert
    verify(restClient, times(1)).get();
}

}
