package com.weatherapp.backend.service;

import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.dto.WeatherResponseDto;
import com.weatherapp.backend.exception.CityNotFoundException;
import com.weatherapp.backend.exception.WeatherProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Ejecuta los servicios contra un servidor HTTP simulado para cubrir la construcción real de
 * las URIs y la deserialización JSON de las respuestas de Open-Meteo.
 */
class ServicesHttpIntegrationTest {

    private static final String GEO_URL = "http://geo.test/v1";
    private static final String FORECAST_URL = "http://forecast.test/v1";

    private static final String GEO_JSON = """
            {"results":[{"id":1,"name":"Medellín","latitude":6.2442,"longitude":-75.5812,
            "country":"Colombia","admin1":"Antioquia","unknown":"x"}]}""";

    private static final String FORECAST_JSON = """
            {"latitude":6.2,"current":{"temperature_2m":22.5,"apparent_temperature":23.1,
            "relative_humidity_2m":60,"wind_speed_10m":11.2,"weather_code":3,"is_day":0},
            "daily":{"time":["2026-01-01","2026-01-02"],"temperature_2m_max":[25.0,26.0],
            "temperature_2m_min":[15.0,16.0],"weather_code":[1,61]}}""";

    private MockRestServiceServer geoServer;
    private MockRestServiceServer forecastServer;
    private GeocodingService geocodingService;
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        RestClient.Builder geoBuilder = RestClient.builder().baseUrl(GEO_URL);
        RestClient.Builder forecastBuilder = RestClient.builder().baseUrl(FORECAST_URL);
        geoServer = MockRestServiceServer.bindTo(geoBuilder).build();
        forecastServer = MockRestServiceServer.bindTo(forecastBuilder).build();
        geocodingService = new GeocodingService(geoBuilder.build());
        weatherService = new WeatherService(forecastBuilder.build(), geocodingService);
    }

    @Test
    void findCity_debeConsultarGeocodingYDeserializarLaRespuesta() {
        geoServer.expect(requestTo(org.hamcrest.Matchers.startsWith(GEO_URL + "/search")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("count", "1"))
                .andExpect(queryParam("language", "es"))
                .andRespond(withSuccess(GEO_JSON, MediaType.APPLICATION_JSON));

        GeocodingResult result = geocodingService.findCity("Medellín");

        assertEquals("Medellín", result.name());
        assertEquals("Antioquia", result.admin1());
        assertEquals(6.2442, result.latitude());
        geoServer.verify();
    }

    @Test
    void findCity_debeLanzarExcepcionCuandoNoHayResultados() {
        geoServer.expect(requestTo(org.hamcrest.Matchers.startsWith(GEO_URL + "/search")))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertThrows(CityNotFoundException.class, () -> geocodingService.findCity("Nada"));
    }

    @Test
    void findCity_debeLanzarExcepcionCuandoResultadosEstaVacio() {
        geoServer.expect(requestTo(org.hamcrest.Matchers.startsWith(GEO_URL + "/search")))
                .andRespond(withSuccess("{\"results\":[]}", MediaType.APPLICATION_JSON));

        assertThrows(CityNotFoundException.class, () -> geocodingService.findCity("Nada"));
    }

    @Test
    void searchCities_debePedirOchoResultados() {
        geoServer.expect(requestTo(org.hamcrest.Matchers.startsWith(GEO_URL + "/search")))
                .andExpect(queryParam("count", "8"))
                .andRespond(withSuccess(GEO_JSON, MediaType.APPLICATION_JSON));

        List<GeocodingResult> results = geocodingService.searchCities("Med");

        assertEquals(1, results.size());
        geoServer.verify();
    }

    @Test
    void searchCities_debeRetornarListaVaciaCuandoNoHayCampoResults() {
        geoServer.expect(requestTo(org.hamcrest.Matchers.startsWith(GEO_URL + "/search")))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertTrue(geocodingService.searchCities("zzz").isEmpty());
    }

    @Test
    void getWeatherForCity_debeComponerGeocodingYPronostico() {
        geoServer.expect(requestTo(org.hamcrest.Matchers.startsWith(GEO_URL + "/search")))
                .andRespond(withSuccess(GEO_JSON, MediaType.APPLICATION_JSON));
        forecastServer.expect(requestTo(org.hamcrest.Matchers.startsWith(FORECAST_URL + "/forecast")))
                .andExpect(queryParam("latitude", "6.2442"))
                .andExpect(queryParam("longitude", "-75.5812"))
                .andExpect(queryParam("timezone", "auto"))
                .andRespond(withSuccess(FORECAST_JSON, MediaType.APPLICATION_JSON));

        WeatherResponseDto weather = weatherService.getWeatherForCity("Medellín");

        assertEquals("Medellín", weather.location().name());
        assertEquals(22.5, weather.current().temperature());
        assertEquals(60, weather.current().humidity());
        assertFalse(weather.current().isDay());
        assertEquals(2, weather.daily().size());
        assertEquals("2026-01-02", weather.daily().get(1).date());
        assertEquals(26.0, weather.daily().get(1).tempMax());
        assertEquals(16.0, weather.daily().get(1).tempMin());
        assertEquals(61, weather.daily().get(1).weatherCode());
        geoServer.verify();
        forecastServer.verify();
    }

    @Test
    void getWeatherForCoordinates_debeConsultarSoloElPronostico() {
        forecastServer.expect(requestTo(org.hamcrest.Matchers.startsWith(FORECAST_URL + "/forecast")))
                .andRespond(withSuccess(FORECAST_JSON, MediaType.APPLICATION_JSON));

        WeatherResponseDto weather = weatherService.getWeatherForCoordinates(6.24, -75.58, "Medellín", "Antioquia", "Colombia");

        assertEquals("Antioquia", weather.location().admin1());
        assertEquals(6.24, weather.location().latitude());
        assertEquals(2, weather.daily().size());
        geoServer.verify();
        forecastServer.verify();
    }

    @Test
    void getWeatherForCoordinates_debeMarcarDiaCuandoIsDayEsUno() {
        String json = FORECAST_JSON.replace("\"is_day\":0", "\"is_day\":1");
        forecastServer.expect(requestTo(org.hamcrest.Matchers.startsWith(FORECAST_URL + "/forecast")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        assertTrue(weatherService.getWeatherForCoordinates(1, 2, "X", null, null).current().isDay());
    }

    @Test
    void getWeatherForCoordinates_debeLanzarExcepcionCuandoFaltaBloqueCurrent() {
        forecastServer.expect(requestTo(org.hamcrest.Matchers.startsWith(FORECAST_URL + "/forecast")))
                .andRespond(withSuccess("{\"daily\":{\"time\":[]}}", MediaType.APPLICATION_JSON));

        WeatherProviderException ex = assertThrows(WeatherProviderException.class,
                () -> weatherService.getWeatherForCoordinates(1, 2, "Lugar", null, null));
        assertEquals("No se pudo obtener el clima para Lugar", ex.getMessage());
    }

    @Test
    void getWeatherForCoordinates_debePropagarErrorDelServidor() {
        forecastServer.expect(requestTo(org.hamcrest.Matchers.startsWith(FORECAST_URL + "/forecast")))
                .andRespond(withServerError());

        assertThrows(org.springframework.web.client.RestClientException.class,
                () -> weatherService.getWeatherForCoordinates(1, 2, "X", null, null));
    }
}
