# Pruebas de aceptación de API con Karate

## Qué verifica esta implementación

Karate envía peticiones HTTP a una instancia real del backend y comprueba estados y contenido JSON. El equipo define estos siete escenarios como criterios de aceptación de la API. Son pruebas de la API integrada, sin automatización de la interfaz de React.

El runner inicia el backend y un servidor HTTP local que sustituye a Open-Meteo. Los controladores, servicios, serialización y manejo de errores del backend son reales. Las temperaturas, fechas y ciudades de los ejemplos son datos de prueba fijos, no observaciones meteorológicas actuales. La suite no comprueba la disponibilidad ni el contrato vigente del proveedor real.

## Ejecución local

Requisito: JDK 17 y conexión para descargar dependencias durante la primera ejecución.

Desde `backend`, en PowerShell:

```powershell
.\gradlew.bat acceptanceTest
```

En Linux o macOS:

```sh
./gradlew acceptanceTest
```

No necesitas iniciar la aplicación manualmente. Se utilizan puertos libres en localhost, por lo que no se requiere detener la aplicación que ya use el puerto 8080. Al finalizar se cierran ambos servidores. La tarea ejecuta siempre los escenarios aunque no cambien los archivos.

`check` incluye pruebas unitarias, de rendimiento y de aceptación. La tarea `test` sigue ejecutando la suite original y generando su cobertura JaCoCo.

## Archivos principales

| Archivo | Responsabilidad |
|---|---|
| `backend/src/acceptanceTest/resources/karate-config.js` | Configuración compartida: dirección del backend de prueba y tiempos de espera de HTTP. |
| `backend/src/acceptanceTest/resources/acceptance/weather.feature` | Los siete escenarios legibles con Given/When/Then y sus verificaciones. |
| `backend/src/acceptanceTest/java/com/weatherapp/backend/acceptance/WeatherAcceptanceTest.java` | Inicia el entorno, ejecuta Karate y hace fallar Gradle si falla un escenario. |
| `backend/src/acceptanceTest/java/com/weatherapp/backend/acceptance/WeatherProviderFixture.java` | Proveedor local con respuestas controladas de geocodificación y pronóstico. |
| `backend/src/acceptanceTest/resources/acceptance/fixtures/` | Respuestas JSON de prueba. |

## Explicación de karate-config.js

Karate ejecuta `fn()` antes de cada escenario y comparte las propiedades del objeto que esta función devuelve.

1. El runner inicia el backend en un puerto libre.
2. Entrega su URL mediante la propiedad `weather.baseUrl`.
3. `karate-config.js` lee esa propiedad y devuelve `{ baseUrl: baseUrl }`.
4. Los escenarios usan `Given url baseUrl`, sin repetir ni fijar el puerto.

Los tiempos de conexión y lectura son límites para las solicitudes de prueba. No son objetivos de rendimiento ni criterios para medir la velocidad de la aplicación.

## Escenarios automatizados

| Identificador | Criterio |
|---|---|
| PA-API-01 | Consultar Bogotá devuelve HTTP 200, ubicación y condiciones actuales esperadas. |
| PA-API-02 | El pronóstico contiene los dos días de la respuesta controlada y sus temperaturas. |
| PA-API-03 | Una búsqueda por coordenadas conserva la ubicación seleccionada. |
| PA-API-04 | La consulta de sugerencias devuelve las ubicaciones esperadas. |
| PA-API-05 | Una ciudad inexistente devuelve HTTP 404 y un mensaje identificable. |
| PA-API-06 | Una consulta vacía de sugerencias devuelve HTTP 200 y una lista vacía. |
| PA-API-07 | Un pronóstico incompleto del proveedor produce HTTP 502 y un mensaje de error. |

Los escenarios PA-API son independientes de los identificadores PA del documento manual. No todos sus criterios son equivalentes: no automatizan clics, teclado, indicadores de carga ni recuperación visual. Esas verificaciones permanecen en `PRUEBAS_ACEPTACION.md`.

## Ejemplo para explicar al profesor

En PA-API-01, `Given` prepara `/api/weather` con la ciudad Bogotá, `When method get` envía la petición y `Then status 200` comprueba que la API responda correctamente. Las instrucciones `match` verifican que el contenido devuelto coincida con la ubicación y el tiempo controlados del ejemplo. La prueba usa los servicios reales del backend, mientras un proveedor local evita depender del clima y de la red externa.

## Reportes y automatización

- Reporte detallado: `backend/build/reports/karate/karate-summary.html`.
- Reporte Gradle del runner: `backend/build/reports/tests/acceptanceTest/index.html`.
- XML del runner: `backend/build/test-results/acceptanceTest/`.
- GitHub Actions ejecuta `acceptanceTest` en el trabajo del backend y publica el artefacto `backend-karate-reports`, incluso si falla la suite.
- Si falla aceptación, el trabajo de backend falla y el trabajo posterior de SonarQube no se ejecuta.
- Las clases Java de aceptación se incluyen como pruebas en SonarQube, con sus binarios y dependencias. Los archivos `.feature` no se presentan como código productivo ni como cobertura unitaria.

## Versiones y fuentes

Se utiliza `io.karatelabs:karate-core:1.5.2` para conservar Java 17. El runner invoca `Runner` desde el JUnit existente, sin añadir otro motor JUnit. Karate 2 requiere Java 21 o posterior.

- Plantilla oficial con Java 17 y Karate 1.5.2: https://github.com/karatelabs/karate-template/blob/main/pom.xml
- Documentación de la línea 1.5.2: https://github.com/karatelabs/karate/tree/v1.5.2
- Migración a Karate 2: https://github.com/karatelabs/karate-v2/blob/main/docs/MIGRATION_GUIDE.md

La implementación no modifica la lógica de producción ni sustituye el análisis estático de SonarQube.

## Verificación realizada el 19 de octubre de 2026

- Karate: 7 escenarios aprobados, 0 fallidos (ejecutados mediante un runner JUnit).
- Suite existente del backend: 51 pruebas aprobadas, 0 fallidas.
- Rendimiento: 5 pruebas aprobadas, 0 fallidas.
- JaCoCo: 125 de 125 líneas cubiertas en el backend, según el reporte de esta ejecución. Este dato local no reemplaza la métrica global de SonarQube.
- Exportación de dependencias para SonarQube: completada, incluido karate-core.

La tarea `acceptanceTest` terminó correctamente. Para verificar las suites existentes en este entorno Windows se precargó el agente Byte Buddy mediante una configuración temporal externa al proyecto, porque la conexión automática de Mockito quedó bloqueada. Al terminar esa ejecución de regresión, Gradle mostró una restricción de acceso al Registro de Windows en su cierre, después de generar los reportes aprobados. La ejecución en GitHub Actions y el nuevo análisis de SonarQube quedan pendientes del push y PR.
