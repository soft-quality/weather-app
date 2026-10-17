package com.weatherapp.backend.dto;

import com.weatherapp.backend.client.dto.CurrentBlock;
import com.weatherapp.backend.client.dto.DailyBlock;
import com.weatherapp.backend.client.dto.ForecastApiResponse;
import com.weatherapp.backend.client.dto.GeocodingApiResponse;
import com.weatherapp.backend.client.dto.GeocodingResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifica el contrato de los records (acceso, igualdad, hashCode y toString). */
class DtoTest {

    @Test
    void locationDto() {
        LocationDto a = new LocationDto("Cali", "Valle", "Colombia", 3.4, -76.5);
        LocationDto b = new LocationDto("Cali", "Valle", "Colombia", 3.4, -76.5);

        assertEquals("Cali", a.name());
        assertEquals("Valle", a.admin1());
        assertEquals("Colombia", a.country());
        assertEquals(3.4, a.latitude());
        assertEquals(-76.5, a.longitude());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new LocationDto("Otra", null, null, 0, 0));
        assertTrue(a.toString().contains("Cali"));
    }

    @Test
    void currentWeatherDto() {
        CurrentWeatherDto a = new CurrentWeatherDto(20, 21, 50, 5, 1, true);

        assertEquals(20, a.temperature());
        assertEquals(21, a.apparentTemperature());
        assertEquals(50, a.humidity());
        assertEquals(5, a.windSpeed());
        assertEquals(1, a.weatherCode());
        assertTrue(a.isDay());
        assertEquals(a, new CurrentWeatherDto(20, 21, 50, 5, 1, true));
        assertEquals(a.hashCode(), new CurrentWeatherDto(20, 21, 50, 5, 1, true).hashCode());
        assertNotEquals(a, new CurrentWeatherDto(20, 21, 50, 5, 1, false));
        assertTrue(a.toString().contains("temperature"));
    }

    @Test
    void dailyForecastDto() {
        DailyForecastDto a = new DailyForecastDto("2026-01-01", 30, 18, 2);

        assertEquals("2026-01-01", a.date());
        assertEquals(30, a.tempMax());
        assertEquals(18, a.tempMin());
        assertEquals(2, a.weatherCode());
        assertEquals(a, new DailyForecastDto("2026-01-01", 30, 18, 2));
        assertEquals(a.hashCode(), new DailyForecastDto("2026-01-01", 30, 18, 2).hashCode());
        assertNotEquals(a, new DailyForecastDto("2026-01-02", 30, 18, 2));
        assertTrue(a.toString().contains("2026-01-01"));
    }

    @Test
    void weatherResponseDto() {
        LocationDto loc = new LocationDto("Cali", null, null, 1, 2);
        CurrentWeatherDto cur = new CurrentWeatherDto(1, 1, 1, 1, 1, false);
        List<DailyForecastDto> daily = List.of(new DailyForecastDto("d", 1, 0, 0));
        WeatherResponseDto a = new WeatherResponseDto(loc, cur, daily);

        assertEquals(loc, a.location());
        assertEquals(cur, a.current());
        assertEquals(daily, a.daily());
        assertEquals(a, new WeatherResponseDto(loc, cur, daily));
        assertEquals(a.hashCode(), new WeatherResponseDto(loc, cur, daily).hashCode());
        assertNotEquals(a, new WeatherResponseDto(loc, cur, List.of()));
        assertTrue(a.toString().contains("Cali"));
    }

    @Test
    void clientDtos() {
        CurrentBlock current = new CurrentBlock(20, 21, 50, 5, 1, 1);
        DailyBlock daily = new DailyBlock(List.of("d"), List.of(1.0), List.of(0.0), List.of(3));
        ForecastApiResponse forecast = new ForecastApiResponse(current, daily);
        GeocodingResult result = new GeocodingResult("Cali", 3.4, -76.5, "Colombia", "Valle");
        GeocodingApiResponse geo = new GeocodingApiResponse(List.of(result));

        assertEquals(20, current.temperature());
        assertEquals(21, current.apparentTemperature());
        assertEquals(50, current.humidity());
        assertEquals(5, current.windSpeed());
        assertEquals(1, current.weatherCode());
        assertEquals(1, current.isDay());
        assertEquals(List.of("d"), daily.time());
        assertEquals(List.of(1.0), daily.temperatureMax());
        assertEquals(List.of(0.0), daily.temperatureMin());
        assertEquals(List.of(3), daily.weatherCode());
        assertEquals(current, forecast.current());
        assertEquals(daily, forecast.daily());
        assertEquals("Cali", result.name());
        assertEquals(3.4, result.latitude());
        assertEquals(-76.5, result.longitude());
        assertEquals("Colombia", result.country());
        assertEquals("Valle", result.admin1());
        assertEquals(List.of(result), geo.results());

        assertEquals(current, new CurrentBlock(20, 21, 50, 5, 1, 1));
        assertEquals(current.hashCode(), new CurrentBlock(20, 21, 50, 5, 1, 1).hashCode());
        assertEquals(daily, new DailyBlock(List.of("d"), List.of(1.0), List.of(0.0), List.of(3)));
        assertEquals(daily.hashCode(), new DailyBlock(List.of("d"), List.of(1.0), List.of(0.0), List.of(3)).hashCode());
        assertEquals(forecast, new ForecastApiResponse(current, daily));
        assertEquals(forecast.hashCode(), new ForecastApiResponse(current, daily).hashCode());
        assertEquals(result, new GeocodingResult("Cali", 3.4, -76.5, "Colombia", "Valle"));
        assertEquals(result.hashCode(), new GeocodingResult("Cali", 3.4, -76.5, "Colombia", "Valle").hashCode());
        assertEquals(geo, new GeocodingApiResponse(List.of(result)));
        assertEquals(geo.hashCode(), new GeocodingApiResponse(List.of(result)).hashCode());
        assertTrue(current.toString().contains("20"));
        assertTrue(daily.toString().contains("d"));
        assertTrue(forecast.toString().contains("current"));
        assertTrue(result.toString().contains("Cali"));
        assertTrue(geo.toString().contains("Cali"));
    }
}
