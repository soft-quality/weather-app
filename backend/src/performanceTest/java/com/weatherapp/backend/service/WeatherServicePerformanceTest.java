package com.weatherapp.backend.service;

import com.weatherapp.backend.client.dto.CurrentBlock;
import com.weatherapp.backend.client.dto.DailyBlock;
import com.weatherapp.backend.client.dto.ForecastApiResponse;
import com.weatherapp.backend.client.dto.GeocodingResult;
import com.weatherapp.backend.dto.WeatherResponseDto;
import com.weatherapp.backend.exception.CityNotFoundException;
import com.weatherapp.backend.exception.WeatherProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 *  PRUEBAS DE PERFORMANCE — WeatherService
 * ============================================================
 *
 * PROPÓSITO
 * ---------
 * Estas pruebas NO verifican corrección funcional (eso lo hacen
 * las pruebas unitarias del compañero). Aquí medimos:
 *   1. Tiempo de respuesta promedio de la lógica de negocio.
 *   2. Comportamiento del servicio bajo carga concurrente.
 *   3. Rendimiento al manejar errores en serie.
 *
 * PATRÓN APLICADO: AAA (Arrange – Act – Assert)
 * ----------------------------------------------
 * Cada prueba está dividida en tres bloques comentados:
 *  - Arrange : preparar datos de entrada y dobles de prueba.
 *  - Act     : ejecutar la acción bajo medición.
 *  - Assert  : verificar resultado Y que el tiempo esté dentro
 *              del umbral establecido.
 *
 * POR QUÉ SE USAN MOCKS Y NO LA API REAL
 * ----------------------------------------
 * Si se midiera contra Open-Meteo real, el resultado dependería
 * de la latencia de internet (variable, fuera de nuestro control).
 * El mock elimina ese ruido: medimos SOLO el overhead de nuestro
 * código (mapeos, validaciones, lógica de negocio), lo cual es
 * reproducible y comparable entre ejecuciones.
 *
 * HERRAMIENTA: Mockito (mocks manuales de RestClient)
 * BUILD: ./gradlew performanceTest
 */
class WeatherServicePerformanceTest {

    // ---------------------------------------------------------------
    // Fixtures reutilizados en todas las pruebas
    // ---------------------------------------------------------------

    /** Respuesta de geocodificación simulada para Medellín */
    private static final GeocodingResult MEDELLIN = new GeocodingResult(
            "Medellín", 6.2518, -75.5636, "Colombia", "Antioquia");

    /** Bloque de clima actual simulado */
    private static final CurrentBlock CURRENT = new CurrentBlock(
            24.5, 23.0, 62, 10.2, 1, 1);

    /** Bloque de pronóstico diario simulado (7 días) */
    private static final DailyBlock DAILY = new DailyBlock(
            List.of("2026-08-16", "2026-08-17", "2026-08-18",
                    "2026-08-19", "2026-08-20", "2026-08-21", "2026-08-22"),
            List.of(28.0, 27.5, 29.0, 26.0, 27.0, 28.5, 30.0),
            List.of(18.0, 17.5, 19.0, 16.0, 17.0, 18.5, 20.0),
            List.of(1, 2, 0, 3, 1, 0, 2));

    /** Respuesta completa de la API de pronóstico */
    private static final ForecastApiResponse FORECAST = new ForecastApiResponse(CURRENT, DAILY);

    private WeatherService weatherService;
    private GeocodingService geocodingServiceMock;

    @BeforeEach
    void setUp() {
        // Creamos mocks de los colaboradores externos
        geocodingServiceMock = mock(GeocodingService.class);

        // RestClient es final, necesitamos un doble manual via builder
        RestClient forecastRestClientMock = buildForecastRestClientMock();

        weatherService = new WeatherService(forecastRestClientMock, geocodingServiceMock);
    }

    // ================================================================
    // PRUEBA 1 — Tiempo de respuesta promedio bajo carga secuencial
    // ================================================================

    /**
     * Verifica que getWeatherForCity() responda en promedio por debajo
     * de 5 ms cuando el cliente externo es instantáneo (mock).
     * Mide únicamente el overhead de nuestro código Java.
     */
    @Test
    void getWeatherForCity_debeResponderEnPromedioBajo5ms_en5000Iteraciones() {
        // ── Arrange ──────────────────────────────────────────────────
        when(geocodingServiceMock.findCity("Medellín")).thenReturn(MEDELLIN);
        int iteraciones = 5_000;

        // Warm-up: descarta las primeras llamadas para evitar medir
        // el costo de inicialización de la JVM (carga de clases, JIT).
        for (int i = 0; i < 500; i++) {
            weatherService.getWeatherForCity("Medellín");
        }

        // ── Act ──────────────────────────────────────────────────────
        long inicioNs = System.nanoTime();
        for (int i = 0; i < iteraciones; i++) {
            weatherService.getWeatherForCity("Medellín");
        }
        long finNs = System.nanoTime();

        // ── Assert ───────────────────────────────────────────────────
        double promedioMs = (finNs - inicioNs) / 1_000_000.0 / iteraciones;
        System.out.printf("[PERF] getWeatherForCity — promedio: %.4f ms (%d iteraciones)%n",
                promedioMs, iteraciones);

        assertTrue(promedioMs < 5.0,
                () -> String.format(
                        "Tiempo promedio demasiado alto: %.4f ms (umbral: 5.0 ms). " +
                        "Revisar posibles regresiones en la lógica de mapeo.", promedioMs));
    }

    // ================================================================
    // PRUEBA 2 — Comportamiento bajo concurrencia (50 hilos simultáneos)
    // ================================================================

    /**
     * Simula 50 solicitudes concurrentes con 10 ms de latencia artificial
     * en el cliente externo (representando una API real lenta).
     * Valida que:
     *  - Ningún hilo lanza excepción por condiciones de carrera.
     *  - El total de tiempo es menor a 1 segundo (paralelismo efectivo).
     */
    @Test
    void getWeatherForCity_debeSoportar50LlamadasConcurrentesSinErrores() throws InterruptedException {
        // ── Arrange ──────────────────────────────────────────────────
        when(geocodingServiceMock.findCity(anyString())).thenAnswer(inv -> {
            Thread.sleep(10); // simula latencia de geocodificación
            return MEDELLIN;
        });

        int hilos = 50;
        ExecutorService executor = Executors.newFixedThreadPool(hilos);
        CountDownLatch latch = new CountDownLatch(hilos);
        AtomicInteger errores = new AtomicInteger(0);
        AtomicInteger exitosos = new AtomicInteger(0);

        // ── Act ──────────────────────────────────────────────────────
        long inicioNs = System.nanoTime();
        for (int i = 0; i < hilos; i++) {
            executor.submit(() -> {
                try {
                    WeatherResponseDto resultado = weatherService.getWeatherForCity("Medellín");
                    if (resultado != null) exitosos.incrementAndGet();
                } catch (Exception e) {
                    errores.incrementAndGet();
                    System.err.println("[PERF] Error en hilo: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        boolean terminoATiempo = latch.await(5, TimeUnit.SECONDS);
        long finNs = System.nanoTime();
        executor.shutdown();

        // ── Assert ───────────────────────────────────────────────────
        double totalMs = (finNs - inicioNs) / 1_000_000.0;
        System.out.printf("[PERF] Concurrencia 50 hilos — total: %.2f ms, exitosos: %d, errores: %d%n",
                totalMs, exitosos.get(), errores.get());

        assertTrue(terminoATiempo,
                "No todos los hilos completaron dentro del tiempo límite de 5 segundos");
        assertEquals(0, errores.get(),
                "Se produjeron " + errores.get() + " errores durante la ejecución concurrente");
        assertEquals(hilos, exitosos.get(),
                "No todos los hilos retornaron un resultado válido");
        assertTrue(totalMs < 1_000,
                () -> String.format("El tiempo total fue %.2f ms; se esperaba menos de 1000 ms", totalMs));
    }

    // ================================================================
    // PRUEBA 3 — Rendimiento con coordenadas directas
    // ================================================================

    /**
     * Mide el rendimiento de getWeatherForCoordinates(), que omite
     * el paso de geocodificación. Debe ser más rápido que getWeatherForCity().
     */
    @Test
    void getWeatherForCoordinates_debeSerMasRapidoQueGetWeatherForCity() {
        // ── Arrange ──────────────────────────────────────────────────
        when(geocodingServiceMock.findCity(anyString())).thenReturn(MEDELLIN);
        int iteraciones = 3_000;

        // Warm-up
        for (int i = 0; i < 300; i++) {
            weatherService.getWeatherForCoordinates(6.25, -75.56, "Medellín", "Antioquia", "Colombia");
            weatherService.getWeatherForCity("Medellín");
        }

        // ── Act ──────────────────────────────────────────────────────
        long inicioCoords = System.nanoTime();
        for (int i = 0; i < iteraciones; i++) {
            weatherService.getWeatherForCoordinates(6.25, -75.56, "Medellín", "Antioquia", "Colombia");
        }
        long finCoords = System.nanoTime();

        long inicioCiudad = System.nanoTime();
        for (int i = 0; i < iteraciones; i++) {
            weatherService.getWeatherForCity("Medellín");
        }
        long finCiudad = System.nanoTime();

        // ── Assert ───────────────────────────────────────────────────
        double promedioCoords = (finCoords - inicioCoords) / 1_000_000.0 / iteraciones;
        double promedioCiudad = (finCiudad - inicioCiudad) / 1_000_000.0 / iteraciones;

        System.out.printf("[PERF] getWeatherForCoordinates: %.4f ms promedio%n", promedioCoords);
        System.out.printf("[PERF] getWeatherForCity:        %.4f ms promedio%n", promedioCiudad);

        assertTrue(promedioCoords < 5.0,
                () -> String.format("getWeatherForCoordinates muy lento: %.4f ms", promedioCoords));
        assertTrue(promedioCiudad < 5.0,
                () -> String.format("getWeatherForCity muy lento: %.4f ms", promedioCiudad));
    }

    // ================================================================
    // PRUEBA 4 — Rendimiento al manejar errores (CityNotFoundException)
    // ================================================================

    /**
     * Verifica que el manejo de CityNotFoundException sea rápido incluso
     * en volumen alto. Las excepciones mal manejadas pueden degradar
     * significativamente el rendimiento (stack trace allocation).
     */
    @Test
    void getWeatherForCity_manejarCityNotFoundEnSerieDebeSerEficiente() {
        // ── Arrange ──────────────────────────────────────────────────
        when(geocodingServiceMock.findCity("CiudadInexistente"))
                .thenThrow(new CityNotFoundException("CiudadInexistente"));
        int iteraciones = 2_000;

        // Warm-up
        for (int i = 0; i < 200; i++) {
            assertThrows(CityNotFoundException.class,
                    () -> weatherService.getWeatherForCity("CiudadInexistente"));
        }

        // ── Act ──────────────────────────────────────────────────────
        long inicioNs = System.nanoTime();
        for (int i = 0; i < iteraciones; i++) {
            assertThrows(CityNotFoundException.class,
                    () -> weatherService.getWeatherForCity("CiudadInexistente"));
        }
        long finNs = System.nanoTime();

        // ── Assert ───────────────────────────────────────────────────
        double promedioMs = (finNs - inicioNs) / 1_000_000.0 / iteraciones;
        System.out.printf("[PERF] Manejo de CityNotFoundException — promedio: %.4f ms (%d iteraciones)%n",
                promedioMs, iteraciones);

        assertTrue(promedioMs < 10.0,
                () -> String.format(
                        "El manejo de errores es demasiado lento: %.4f ms (umbral: 10.0 ms). " +
                        "Revisar si hay logging excesivo en el stack de excepciones.", promedioMs));
    }

    // ================================================================
    // PRUEBA 5 — Throughput: cuántas solicitudes por segundo
    // ================================================================

    /**
     * Mide el throughput (solicitudes/segundo) del servicio durante
     * 2 segundos. Sirve como indicador de capacidad máxima teórica
     * de la lógica de negocio sin el cuello de botella de red.
     */
    @Test
    void getWeatherForCity_debeTenerThroughputMayorA1000SolicitudesPorSegundo() {
        // ── Arrange ──────────────────────────────────────────────────
        when(geocodingServiceMock.findCity(anyString())).thenReturn(MEDELLIN);
        long duracionMs = 2_000;

        // Warm-up
        for (int i = 0; i < 500; i++) {
            weatherService.getWeatherForCity("Medellín");
        }

        // ── Act ──────────────────────────────────────────────────────
        long inicio = System.currentTimeMillis();
        long fin = inicio + duracionMs;
        int solicitudes = 0;

        while (System.currentTimeMillis() < fin) {
            weatherService.getWeatherForCity("Medellín");
            solicitudes++;
        }

        long tiempoRealMs = System.currentTimeMillis() - inicio;

        // ── Assert ───────────────────────────────────────────────────
        double throughput = solicitudes / (tiempoRealMs / 1_000.0);
        System.out.printf("[PERF] Throughput: %.0f solicitudes/segundo (%d en %d ms)%n",
                throughput, solicitudes, tiempoRealMs);

        assertTrue(throughput > 1_000,
                () -> String.format(
                        "Throughput insuficiente: %.0f req/s (mínimo esperado: 1000 req/s). " +
                        "Posible regresión de rendimiento en la capa de mapeo.", throughput));
    }

    // ================================================================
    // Helpers privados
    // ================================================================

    /**
     * Construye un mock de RestClient que devuelve FORECAST para
     * cualquier llamada .get().uri(...).retrieve().body(ForecastApiResponse.class).
     * Mockito no puede mockear RestClient directamente porque es una
     * interfaz con métodos encadenados (builder pattern), así que se
     * mockea cada eslabón de la cadena.
     */
    @SuppressWarnings("unchecked")
    private RestClient buildForecastRestClientMock() {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec<?> uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(java.util.function.Function.class))).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ForecastApiResponse.class)).thenReturn(FORECAST);

        return restClient;
    }
}
