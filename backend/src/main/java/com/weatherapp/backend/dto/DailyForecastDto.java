package com.weatherapp.backend.dto;

public record DailyForecastDto(
        String date,
        double tempMax,
        double tempMin,
        int weatherCode
) {
}
