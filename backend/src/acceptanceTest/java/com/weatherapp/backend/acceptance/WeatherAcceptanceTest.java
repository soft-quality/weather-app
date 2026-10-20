package com.weatherapp.backend.acceptance;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.weatherapp.backend.BackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherAcceptanceTest {

    @Test
    void apiSatisfaceLosCriteriosDeAceptacion() throws Exception {
        try (WeatherProviderFixture provider = new WeatherProviderFixture()) {
            try (ConfigurableApplicationContext app = new SpringApplicationBuilder(BackendApplication.class)
                    .run("--server.port=0",
                            "--weather.api.geocoding-url=" + provider.baseUrl(),
                            "--weather.api.forecast-url=" + provider.baseUrl())) {
                int port = ((WebServerApplicationContext) app).getWebServer().getPort();
                Results results = Runner.path("classpath:acceptance/weather.feature")
                        .systemProperty("weather.baseUrl", "http://127.0.0.1:" + port)
                        .reportDir(System.getProperty("karate.reportDir"))
                        .outputJunitXml(true)
                        .parallel(1);
                assertEquals(0, results.getFailCount(), results.getErrorMessages());
                assertEquals(7, results.getScenariosTotal(), "Deben ejecutarse los siete escenarios");
            }
        }
    }
}
