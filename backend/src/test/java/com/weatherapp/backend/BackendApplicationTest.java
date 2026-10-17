package com.weatherapp.backend;

import com.weatherapp.backend.controller.CityController;
import com.weatherapp.backend.controller.WeatherController;
import com.weatherapp.backend.service.GeocodingService;
import com.weatherapp.backend.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BackendApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads_debeCargarLosBeansPrincipales() {
        assertNotNull(context.getBean(WeatherController.class));
        assertNotNull(context.getBean(CityController.class));
        assertNotNull(context.getBean(WeatherService.class));
        assertNotNull(context.getBean(GeocodingService.class));
    }
}
