package com.weatherapp.backend.exception;

public class WeatherProviderException extends RuntimeException {

    public WeatherProviderException(String message) {
        super(message);
    }
}
