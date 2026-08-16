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
    public WeatherResponseDto getWeather(@RequestParam String city) {
        return weatherService.getWeatherForCity(city);
    }
}
