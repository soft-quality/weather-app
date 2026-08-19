# APIs utilizadas

## Elección: Open-Meteo

El backend consume la [Open-Meteo API](https://open-meteo.com/). Se eligió sobre alternativas como OpenWeatherMap por:

- **No requiere API key.** No hay secretos que gestionar en `application.properties`, variables de entorno, ni en el pipeline de Gradle/CI — ideal para un proyecto académico que otros deben poder clonar y correr sin pedir credenciales.
- **Gratuita para uso no comercial**, sin límite de peticiones estricto para volúmenes bajos (uso de desarrollo/pruebas).
- Combina geocodificación (nombre de ciudad → coordenadas) y pronóstico del clima en dos endpoints simples, JSON puro, sin SDK.

Requisito de atribución (uso gratuito): mostrar "Weather data by Open-Meteo.com" — se referencia en el footer del frontend y aquí.

## Endpoints consumidos

### 1. Geocoding API — resolver ciudad → coordenadas

```
GET https://geocoding-api.open-meteo.com/v1/search
```

| Parámetro | Valor usado | Descripción |
|---|---|---|
| `name` | ciudad ingresada por el usuario | término de búsqueda |
| `count` | `1` | solo el resultado más relevante |
| `language` | `es` | nombres en español cuando estén disponibles |
| `format` | `json` | formato de respuesta |

Respuesta relevante:

```json
{
  "results": [
    { "name": "Bogotá", "latitude": 4.60971, "longitude": -74.08175, "country": "Colombia", "admin1": "Bogota D.C." }
  ]
}
```

Si `results` viene vacío o ausente, el backend responde `404` con `CityNotFoundException`.

### 2. Forecast API — clima actual y pronóstico diario

```
GET https://api.open-meteo.com/v1/forecast
```

| Parámetro | Valor usado | Descripción |
|---|---|---|
| `latitude`, `longitude` | de la respuesta de geocoding | ubicación consultada |
| `current` | `temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m` | condiciones actuales |
| `daily` | `weather_code,temperature_2m_max,temperature_2m_min` | pronóstico de los próximos días |
| `timezone` | `auto` | ajusta fechas/horas a la ubicación consultada |

Los códigos de clima (`weather_code`) siguen la tabla estándar **WMO**, documentada en https://open-meteo.com/en/docs y mapeada en `frontend/src/utils/weatherCodes.js`.

## Flujo end-to-end

```
Frontend (React)  →  GET /api/weather?city={ciudad}  →  Backend (Spring Boot)
                                                            │
                                                            ├─ 1) Geocoding API (Open-Meteo)  → lat/lon
                                                            └─ 2) Forecast API (Open-Meteo)    → clima actual + pronóstico
                                                            │
                              Frontend  ←  JSON unificado (WeatherResponseDto)  ←──┘
```

El frontend nunca llama directamente a Open-Meteo: todo pasa por el backend, que centraliza la lógica de consumo, mapea la respuesta a un contrato propio (`WeatherResponseDto`) y maneja errores de forma consistente.

## Contrato de respuesta del backend

`GET /api/weather?city={ciudad}`

```json
{
  "location": { "name": "Bogotá", "country": "Colombia", "latitude": 4.60971, "longitude": -74.08175 },
  "current": {
    "temperature": 14.5,
    "apparentTemperature": 13.8,
    "humidity": 77,
    "windSpeed": 6.2,
    "weatherCode": 3,
    "isDay": false
  },
  "daily": [
    { "date": "2026-08-15", "tempMax": 21.1, "tempMin": 12.1, "weatherCode": 53 }
  ]
}
```

Errores:

| Status | Caso |
|---|---|
| `400` | falta el parámetro `city` |
| `404` | la ciudad no fue encontrada en el geocoding |
| `502` | Open-Meteo no devolvió datos válidos |

## Notas para pruebas futuras

Este documento describe el contrato externo con el que deberán trabajar las pruebas unitarias (mockeando `RestClient` en `GeocodingService`/`WeatherService`) y las pruebas de performance (contra el endpoint `/api/weather` del backend).
