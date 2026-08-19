# Sustentación — Pruebas de Performance

## ¿Qué se hizo?

Se implementaron **5 pruebas de performance** sobre `WeatherService`, el componente central del backend, ubicadas en el source set `src/performanceTest/java` del módulo `backend`.

---

## Herramientas utilizadas

| Herramienta | Versión | Rol |
|---|---|---|
| **JUnit 5** | 5.x (vía Spring Boot) | Framework de ejecución de pruebas |
| **Mockito** | 5.11.0 | Dobles de prueba (mocks) del cliente HTTP externo |
| **Gradle** | 8.7 | Build tool, ejecuta `./gradlew performanceTest` |

---

## ¿Por qué Mockito y no la API real?

Si se midiera contra Open-Meteo real, el tiempo dependería de la latencia de internet (variable, fuera de control). El mock elimina ese ruido: **se mide solo el overhead de nuestro código Java** (mapeos, validaciones, lógica), lo cual es reproducible y comparable entre ejecuciones.

---

## Patrón AAA aplicado

Cada prueba tiene tres bloques explícitos y comentados:

```java
// ── Arrange ──  preparar mocks y datos de entrada
// ── Act ──────  ejecutar la operación bajo medición
// ── Assert ───  verificar resultado Y tiempo dentro del umbral
```

---

## Las 5 pruebas y qué miden

### 1. `getWeatherForCity_debeResponderEnPromedioBajo5ms_en5000Iteraciones`
**Qué mide:** tiempo de respuesta promedio de la lógica de negocio en ejecución secuencial.  
**Cómo:** 5 000 llamadas consecutivas con warm-up previo (descarta JIT y carga de clases).  
**Umbral:** promedio < 5 ms por llamada.  
**Por qué este umbral:** un servicio REST típico debería procesar su lógica interna en microsegundos; 5 ms es generoso, cualquier regresión grave lo superaría.

### 2. `getWeatherForCity_debeSoportar50LlamadasConcurrentesSinErrores`
**Qué mide:** comportamiento bajo carga concurrente de 50 hilos simultáneos.  
**Cómo:** `ExecutorService` con 50 hilos, `CountDownLatch` para sincronizar, `AtomicInteger` para contar errores y éxitos thread-safe.  
**Umbral:** 0 errores, 50 éxitos, tiempo total < 1 segundo.  
**Por qué:** detecta condiciones de carrera (*race conditions*) o cuellos de botella bajo concurrencia.

### 3. `getWeatherForCoordinates_debeSerMasRapidoQueGetWeatherForCity`
**Qué mide:** que `getWeatherForCoordinates()` (sin geocodificación) y `getWeatherForCity()` (con geocodificación) ambas son rápidas, permitiendo comparar su rendimiento relativo.  
**Umbral:** ambas < 5 ms promedio.  
**Por qué:** valida que agregar el paso de geocodificación no introduce una regresión significativa.

### 4. `getWeatherForCity_manejarCityNotFoundEnSerieDebeSerEficiente`
**Qué mide:** rendimiento del manejo de excepciones `CityNotFoundException` en serie.  
**Umbral:** < 10 ms por llamada (más generoso porque la creación de excepciones es costosa).  
**Por qué:** las excepciones con stack traces largos pueden degradar el rendimiento si se lanzan en volumen alto; este test lo detectaría.

### 5. `getWeatherForCity_debeTenerThroughputMayorA1000SolicitudesPorSegundo`
**Qué mide:** throughput (solicitudes/segundo) durante 2 segundos continuos.  
**Umbral:** > 1 000 solicitudes/segundo.  
**Por qué:** da un indicador de capacidad máxima teórica de la lógica de negocio, útil para dimensionar cuántas instancias del servicio se necesitarían en producción.

---

## Factores de calidad cubiertos

| Factor | Cómo se cubre |
|---|---|
| **Rendimiento (Performance)** | Umbrales explícitos de tiempo de respuesta y throughput |
| **Confiabilidad bajo carga** | Prueba de 50 hilos concurrentes sin errores |
| **Robustez ante errores** | Rendimiento de manejo de excepciones en serie |
| **Testabilidad** | Mocks inyectados, sin dependencia de red real |
| **Trazabilidad** | Salida `[PERF]` con métricas reales en cada ejecución |

---

## Cómo ejecutar

```bash
# Solo pruebas de performance
./gradlew performanceTest

# Pruebas unitarias + performance (build completo)
./gradlew check
```

La salida muestra líneas `[PERF]` con las métricas reales, por ejemplo:

```
[PERF] getWeatherForCity — promedio: 0.0023 ms (5000 iteraciones)
[PERF] Concurrencia 50 hilos — total: 312.45 ms, exitosos: 50, errores: 0
[PERF] Throughput: 87432 solicitudes/segundo (174864 en 2001 ms)
```

---

## Integración con el flujo de ramas

```
develop  →  release  →  main
   ↑
   └── aquí se agregan las pruebas de performance
       y se validan antes de pasar a release
```

- En `develop`: se implementan las pruebas.
- En `release`: se corre `./gradlew check` para validar que pasan antes de fusionar a `main`.
- En `main`: solo llega código con todas las pruebas en verde.
