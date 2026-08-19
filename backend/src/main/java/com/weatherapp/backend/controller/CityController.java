package com.weatherapp.backend.controller;

import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.dto.LocationDto;
import com.weatherapp.backend.service.GeocodingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final GeocodingService geocodingService;

    public CityController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @GetMapping
    public List<LocationDto> searchCities(@RequestParam(required = false) String query) {
        return geocodingService.searchCities(query).stream()
                .map(this::toLocationDto)
                .toList();
    }

    private LocationDto toLocationDto(GeocodingResult result) {
        return new LocationDto(result.name(), result.admin1(), result.country(), result.latitude(), result.longitude());
    }
}
