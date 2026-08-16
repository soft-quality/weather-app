package com.weatherapp.backend.controller;

import com.weatherapp.backend.dto.WeatherResponseDto;
import com.weatherapp.backend.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public WeatherResponseDto getWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String admin1,
            @RequestParam(required = false) String country) {
        if (lat != null && lon != null) {
            return weatherService.getWeatherForCoordinates(lat, lon, name, admin1, country);
        }
        return weatherService.getWeatherForCity(city);
    }
}
