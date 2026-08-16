package com.weatherapp.backend.service;

import com.weatherapp.backend.client.dto.CurrentBlock;
import com.weatherapp.backend.client.dto.DailyBlock;
import com.weatherapp.backend.client.dto.ForecastApiResponse;
import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.dto.CurrentWeatherDto;
import com.weatherapp.backend.dto.DailyForecastDto;
import com.weatherapp.backend.dto.LocationDto;
import com.weatherapp.backend.dto.WeatherResponseDto;
import com.weatherapp.backend.exception.WeatherProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestClient forecastRestClient;
    private final GeocodingService geocodingService;

    public WeatherService(@Qualifier("forecastRestClient") RestClient forecastRestClient,
                           GeocodingService geocodingService) {
        this.forecastRestClient = forecastRestClient;
        this.geocodingService = geocodingService;
    }

    public WeatherResponseDto getWeatherForCity(String cityName) {
        GeocodingResult city = geocodingService.findCity(cityName);

        logger.info("Consultando el clima para '{}' ({}, {})", city.name(), city.latitude(), city.longitude());

        ForecastApiResponse forecast = forecastRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", city.latitude())
                        .queryParam("longitude", city.longitude())
                        .queryParam("current", "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m")
                        .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min")
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(ForecastApiResponse.class);

        if (forecast == null || forecast.current() == null || forecast.daily() == null) {
            throw new WeatherProviderException("No se pudo obtener el clima para " + cityName);
        }

        return toWeatherResponseDto(city, forecast);
    }

    private WeatherResponseDto toWeatherResponseDto(GeocodingResult city, ForecastApiResponse forecast) {
        LocationDto location = new LocationDto(city.name(), city.country(), city.latitude(), city.longitude());
        CurrentWeatherDto current = toCurrentWeatherDto(forecast.current());
        List<DailyForecastDto> daily = toDailyForecastList(forecast.daily());

        return new WeatherResponseDto(location, current, daily);
    }

    private CurrentWeatherDto toCurrentWeatherDto(CurrentBlock current) {
        return new CurrentWeatherDto(
                current.temperature(),
                current.apparentTemperature(),
                current.humidity(),
                current.windSpeed(),
                current.weatherCode(),
                current.isDay() == 1
        );
    }

    private List<DailyForecastDto> toDailyForecastList(DailyBlock daily) {
        List<DailyForecastDto> result = new ArrayList<>();
        for (int i = 0; i < daily.time().size(); i++) {
            result.add(new DailyForecastDto(
                    daily.time().get(i),
                    daily.temperatureMax().get(i),
                    daily.temperatureMin().get(i),
                    daily.weatherCode().get(i)
            ));
        }
        return result;
    }
}
