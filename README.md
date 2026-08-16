# Weather App

Aplicación del clima desarrollada como proyecto de la asignatura **Calidad de Software**. Su objetivo principal no es la aplicación en sí, sino servir como base para practicar y demostrar buenas prácticas de testing:

- **Pruebas unitarias** aplicando el patrón **AAA** (Arrange, Act, Assert).
- **Pruebas de performance** sobre los componentes críticos de la aplicación.
- Ejecución y validación de todas las pruebas mediante **Gradle**.

## Objetivos del proyecto

1. Implementar una aplicación del clima funcional (consumo de datos meteorológicos y presentación al usuario).
2. Cubrir la lógica de negocio con pruebas unitarias estructuradas bajo el patrón AAA.
3. Incorporar pruebas de performance para validar tiempos de respuesta y comportamiento bajo carga.
4. Garantizar que el build de Gradle ejecute y pase correctamente todas las pruebas (`./gradlew test`).

## Arquitectura

Proyecto separado en **backend** y **frontend**, comunicados por HTTP/JSON:

```
Frontend (React + Vite)  ──HTTP──►  Backend (Java + Spring Boot + Gradle)  ──HTTP──►  Open-Meteo API
   http://localhost:5173             http://localhost:8080
```

El backend centraliza el consumo de la API externa del clima (geocodificación + pronóstico) y expone un único endpoint propio (`/api/weather`) que el frontend consume. Ver [`docs/API.md`](docs/API.md) para el detalle de las APIs externas usadas, el contrato de respuesta y los códigos de error.

## Estructura del proyecto

```
weather-app/
├── README.md
├── docs/
│   └── API.md                          # APIs externas usadas, contrato y flujo end-to-end
├── backend/                            # Java 17 + Spring Boot + Gradle
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew / gradlew.bat           # wrapper de Gradle (no requiere Gradle instalado)
│   └── src/main/
│       ├── java/com/weatherapp/backend/
│       │   ├── BackendApplication.java # punto de entrada Spring Boot
│       │   ├── controller/
│       │   │   └── WeatherController.java   # GET /api/weather?city=
│       │   ├── service/
│       │   │   ├── GeocodingService.java    # ciudad -> coordenadas (Open-Meteo Geocoding)
│       │   │   └── WeatherService.java      # coordenadas -> clima (Open-Meteo Forecast)
│       │   ├── client/dto/             # DTOs que mapean la respuesta cruda de Open-Meteo
│       │   ├── dto/                    # DTOs propios de la API (contrato hacia el frontend)
│       │   ├── config/                 # RestClient beans, CORS, configuración tipada
│       │   └── exception/              # excepciones de dominio + manejador global (@RestControllerAdvice)
│       └── resources/
│           └── application.properties
└── frontend/                           # React 19 + Vite
    ├── index.html
    ├── package.json
    ├── .env.example                    # VITE_API_BASE_URL
    └── src/
        ├── main.jsx
        ├── App.jsx                     # orquesta búsqueda, loading y error
        ├── index.css                   # tokens de diseño + estilos base + utilidad .glass
        ├── App.css
        ├── components/
        │   ├── SearchBar.jsx/.css      # input de ciudad
        │   ├── WeatherCard.jsx/.css    # clima actual (glassmorphism)
        │   ├── ForecastList.jsx/.css   # pronóstico de próximos días
        │   ├── WeatherIcon.jsx         # iconos SVG por condición climática
        │   ├── Loader.jsx/.css         # estado de carga
        │   └── ErrorMessage.jsx/.css   # estado de error
        ├── services/
        │   └── weatherApi.js           # cliente fetch hacia el backend
        └── utils/
            └── weatherCodes.js         # mapeo de códigos WMO a texto/ícono
```

> Nota: por ahora el proyecto contiene solo la aplicación (sin pruebas todavía). Las carpetas `src/test` de cada módulo se irán agregando en una siguiente etapa, junto con las pruebas de performance.

## Diseño (frontend)

Estilo **glassmorphism**: tarjetas translúcidas con `backdrop-filter: blur()`, bordes sutiles y sombra suave, sobre un fondo con gradiente. Paleta sky blue + acento ámbar (clima/sol), tipografía **Fira Sans**. Tokens de diseño (colores, espaciados, radios) centralizados como CSS custom properties en `frontend/src/index.css`, con soporte de `prefers-color-scheme: dark` y `prefers-reduced-motion`.

## Cómo correr el proyecto

### Backend

Requiere **Java 17+** (verificar que `JAVA_HOME` apunte a esa versión).

```bash
cd backend
./gradlew bootRun
```

Queda disponible en `http://localhost:8080`. Prueba rápida:

```bash
curl "http://localhost:8080/api/weather?city=Bogota"
```

### Frontend

Requiere **Node.js**.

```bash
cd frontend
npm install
cp .env.example .env
npm run dev
```

Queda disponible en `http://localhost:5173` (necesita el backend corriendo).

## Testing (próxima etapa)

- **Framework de pruebas unitarias:** JUnit 5 + Mockito (ya incluidos por el starter de Spring Boot).
- **Patrón aplicado:** AAA (Arrange, Act, Assert) en cada caso de prueba.
- **Pruebas de performance:** por definir (ej. JMH u otra herramienta compatible con Gradle) sobre el endpoint `/api/weather`.
- **Build tool:** Gradle.

```bash
# Ejecutar todas las pruebas
cd backend
./gradlew test

# Ejecutar el build completo
./gradlew build
```

## Flujo de ramas

Este repositorio sigue un flujo de trabajo basado en tres ramas principales:

- `main`: código estable, listo para producción.
- `develop`: rama de integración donde se combinan las nuevas funcionalidades antes de pasar a `release`.
- `release`: rama de preparación de versiones, usada para estabilizar y validar antes de fusionar a `main`.

## Estado

Aplicación base funcional (backend + frontend conectados a Open-Meteo). Pendiente: pruebas unitarias (AAA) y pruebas de performance.
