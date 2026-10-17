package com.weatherapp.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {

    @Test
    void weatherApiProperties_debeExponerLasUrls() {
        WeatherApiProperties props = new WeatherApiProperties("http://geo", "http://forecast");

        assertEquals("http://geo", props.geocodingUrl());
        assertEquals("http://forecast", props.forecastUrl());
        assertEquals(props, new WeatherApiProperties("http://geo", "http://forecast"));
        assertEquals(props.hashCode(), new WeatherApiProperties("http://geo", "http://forecast").hashCode());
        assertTrue(props.toString().contains("http://geo"));
    }

    @Test
    void restClientConfig_debeCrearLosDosClientes() {
        RestClientConfig config = new RestClientConfig();
        WeatherApiProperties props = new WeatherApiProperties("http://geo.test/v1", "http://forecast.test/v1");

        RestClient geocoding = config.geocodingRestClient(props);
        RestClient forecast = config.forecastRestClient(props);

        assertNotNull(geocoding);
        assertNotNull(forecast);
        assertNotSame(geocoding, forecast);
    }

    @Test
    @SuppressWarnings("unchecked")
    void webConfig_debeRegistrarCorsParaApi() {
        WebConfig config = new WebConfig();
        ReflectionTestUtils.setField(config, "allowedOrigins", new String[]{"http://localhost:5173"});
        CorsRegistry registry = new CorsRegistry();

        config.addCorsMappings(registry);

        Map<String, CorsConfiguration> configs =
                (Map<String, CorsConfiguration>) ReflectionTestUtils.invokeMethod(registry, "getCorsConfigurations");
        CorsConfiguration cors = configs.get("/api/**");
        assertNotNull(cors);
        assertEquals(List.of("http://localhost:5173"), cors.getAllowedOrigins());
        assertEquals(List.of("GET"), cors.getAllowedMethods());
    }
}
