package com.weatherapp.backend.dto;

import java.util.List;

public record WeatherResponseDto(
        LocationDto location,
        CurrentWeatherDto current,
        List<DailyForecastDto> daily
) {
}
