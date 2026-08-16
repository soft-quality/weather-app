package com.weatherapp.backend.dto;

public record LocationDto(
        String name,
        String country,
        double latitude,
        double longitude
) {
}
