package com.weatherapp.backend.dto;

public record LocationDto(
        String name,
        String admin1,
        String country,
        double latitude,
        double longitude
) {
}
