package com.weatherapp.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastApiResponse(CurrentBlock current, DailyBlock daily) {
}
