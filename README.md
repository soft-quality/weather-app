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

## Flujo de ramas

Este repositorio sigue un flujo de trabajo basado en tres ramas principales:

- `main`: código estable, listo para producción.
- `develop`: rama de integración donde se combinan las nuevas funcionalidades antes de pasar a `release`.
- `release`: rama de preparación de versiones, usada para estabilizar y validar antes de fusionar a `main`.

## Testing

- **Framework de pruebas unitarias:** por definir (ej. JUnit 5).
- **Patrón aplicado:** AAA (Arrange, Act, Assert) en cada caso de prueba.
- **Pruebas de performance:** por definir (ej. JMH u otra herramienta compatible con Gradle).
- **Build tool:** Gradle.

### Comandos

```bash
# Ejecutar todas las pruebas
./gradlew test

# Ejecutar el build completo
./gradlew build
```

## Estado

Proyecto en fase inicial de configuración.
