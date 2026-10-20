# Pruebas de aceptación — Weather App

## Objetivo y alcance

Comprobar que la aplicación satisface los criterios funcionales del usuario al buscar una ciudad y consultar el tiempo actual y el pronóstico. Estos son escenarios de aceptación para ejecución manual, escritos con Dado/Cuando/Entonces. Este archivo no instala Karate ni incorpora pruebas automatizadas al pipeline.

Estado inicial: todos los escenarios están PENDIENTES DE EJECUCIÓN. La revisión del código no equivale a ejecutar ni aprobar una prueba.

## Preparación

1. Iniciar el backend según README.md (puerto habitual 8080).
2. Iniciar el frontend según README.md (puerto habitual 5173) y abrir su dirección en el navegador. La raíz del backend no es necesariamente la interfaz.
3. Verificar que el frontend apunte al backend y que exista conexión con Open-Meteo.
4. Registrar rama, commit, navegador, fecha y responsable antes de ejecutar.
5. Para cada escenario, guardar evidencia y resultado real. No fijar temperaturas concretas: los datos meteorológicos varían.

## PA-01 — Consultar el tiempo actual de una ciudad

**Necesidad:** Como usuario, quiero conocer las condiciones actuales de una ciudad.

- **Dado** que la aplicación está disponible y el proveedor meteorológico responde.
- **Cuando** escribo `Bogotá` y pulso Buscar, sin seleccionar una sugerencia.
- **Entonces** aparece la ciudad encontrada con su región y país cuando estén disponibles.
- **Y** se muestran temperatura, condición del tiempo, sensación térmica, humedad y velocidad del viento.
- **Y** los valores corresponden a la respuesta de la consulta, con el redondeo utilizado por la interfaz.

**Comprobación:** comparar con la respuesta de `/api/weather?city=Bogot%C3%A1` de esa misma consulta, visible en Red/Network del navegador. No comparar con otra consulta realizada más tarde.

## PA-02 — Mostrar el pronóstico

**Necesidad:** Como usuario, quiero consultar el pronóstico de los próximos días.

- **Dado** que una consulta válida devuelve una lista diaria no vacía.
- **Cuando** termina la búsqueda de la ciudad.
- **Entonces** aparece la sección Próximos días.
- **Y** cada registro recibido muestra su fecha, icono de condición, temperatura máxima y mínima.
- **Y** las fechas y temperaturas coinciden con la respuesta `daily`, considerando el formato y redondeo visual.

## PA-03 — Seleccionar una sugerencia de ciudad

**Necesidad:** Como usuario, quiero distinguir ciudades con nombres similares.

- **Dado** que la búsqueda de sugerencias está disponible.
- **Cuando** escribo al menos dos caracteres de `Bogotá` y dejo de escribir.
- **Entonces** aparece una lista si el servicio devuelve coincidencias.
- **Cuando** selecciono una sugerencia con el mouse.
- **Entonces** el campo muestra el nombre y la región/país disponibles de la ubicación elegida.
- **Y** se cierra la lista y se consulta el tiempo de esa ubicación por sus coordenadas.

**Comprobación:** verificar en Red/Network que `/api/weather` reciba `lat` y `lon` de la sugerencia seleccionada.

## PA-04 — Buscar utilizando el teclado

**Necesidad:** Como usuario, quiero elegir una ciudad sin utilizar el mouse.

- **Dado** que hay sugerencias visibles.
- **Cuando** pulso Flecha abajo o Flecha arriba.
- **Entonces** cambia la sugerencia resaltada.
- **Cuando** pulso Enter con una sugerencia activa.
- **Entonces** se selecciona y se consulta esa ubicación.

**Variante:** volver a abrir las sugerencias y pulsar Escape. La lista debe cerrarse sin iniciar una nueva consulta meteorológica por esa acción.

**Límite:** este escenario verifica interacción con teclado. No certifica por sí solo toda la accesibilidad del componente.

## PA-05 — Evitar una búsqueda vacía

**Necesidad:** Como usuario, quiero evitar consultas sin una ciudad.

- **Dado** que el campo está vacío o contiene solo espacios.
- **Cuando** pulso Buscar o Enter.
- **Entonces** no se envía una nueva petición a `/api/weather`.
- **Y** la aplicación permanece utilizable.

**Comprobación:** revisar Red/Network. Este criterio corresponde al formulario, no afirma que el endpoint valide correctamente todos los parámetros inválidos.

## PA-06 — Informar una ciudad inexistente

**Necesidad:** Como usuario, quiero saber cuándo no se encuentra una ciudad.

- **Dado** que el proveedor no devuelve coincidencias para `zzqv-ciudad-inexistente-987654`.
- **Cuando** envío ese texto con Buscar, sin elegir una sugerencia.
- **Entonces** la aplicación muestra un mensaje que informa que la ciudad no se encontró.
- **Y** termina el estado de carga y permite intentar otra búsqueda.
- **Y** no muestra datos meteorológicos de una consulta anterior como resultado de la búsqueda fallida.

**Comprobación adicional:** la respuesta de `/api/weather` debe ser HTTP 404 con un mensaje de error. Si el proveedor encuentra una coincidencia inesperada, elegir otra entrada sin resultados y registrarla.

## PA-07 — Mostrar el estado de carga

**Necesidad:** Como usuario, quiero saber que la búsqueda está en proceso.

- **Dado** que la respuesta meteorológica tarda lo suficiente para observarla.
- **Cuando** inicio una búsqueda válida.
- **Entonces** aparece el indicador de carga y el botón muestra Buscando… deshabilitado.
- **Cuando** termina la solicitud, correctamente o con error.
- **Entonces** desaparece el indicador y se vuelve a habilitar Buscar.

**Preparación opcional:** utilizar la limitación de velocidad de Red/Network del navegador y restaurarla al terminar. Este caso no mide rendimiento ni establece un tiempo máximo de respuesta.

## PA-08 — Manejar la indisponibilidad del backend y recuperar la consulta

**Necesidad:** Como usuario, quiero recibir un aviso si falla la consulta y poder volver a intentarlo.

- **Dado** que el frontend está abierto y el backend de prueba está temporalmente detenido.
- **Cuando** envío una búsqueda no vacía.
- **Entonces** aparece un mensaje de error, termina el estado de carga y la página sigue utilizable.
- **Cuando** restauro el backend y realizo una búsqueda válida.
- **Entonces** desaparece el error anterior y se muestran los nuevos datos meteorológicos.

**Preparación:** ejecutar únicamente en el entorno local de pruebas. Si la detención afecta a otras personas, usar bloqueo de solicitudes del navegador en lugar de detener el servicio. Restaurar el entorno al finalizar.

## Registro de ejecución

Rama y commit: ____________________
Fecha y responsable: ____________________
Dirección del frontend y backend: ____________________
Navegador: ____________________

| Caso | Resultado | Resultado real / evidencia / incidencia |
|---|---|---|
| PA-01 Tiempo actual | Pendiente | |
| PA-02 Pronóstico | Pendiente | |
| PA-03 Sugerencias | Pendiente | |
| PA-04 Teclado | Pendiente | |
| PA-05 Búsqueda vacía | Pendiente | |
| PA-06 Ciudad inexistente | Pendiente | |
| PA-07 Estado de carga | Pendiente | |
| PA-08 Error y recuperación | Pendiente | |

Resultados permitidos: Aprobado, Fallido, Bloqueado o Pendiente. Aprobar solo cuando todas las verificaciones del escenario se cumplan. Si falta el proveedor o el entorno, registrar Bloqueado y su causa.

## Criterio de aceptación de la entrega

Los ocho escenarios deben estar ejecutados y aprobados, o contar con una excepción expresamente acordada y documentada. Si falla un escenario, registrar el comportamiento observado, corregirlo y repetir las verificaciones afectadas. Esta condición es un criterio propuesto para la actividad académica, no una aprobación ya obtenida.

## Relación con las herramientas existentes

- JUnit/Mockito y Vitest comprueban unidades y componentes específicos.
- Estas pruebas de aceptación verifican necesidades del usuario en el sistema integrado.
- SonarQube revisa estáticamente el código y sus métricas. Un Quality Gate aprobado no demuestra que estos escenarios hayan sido ejecutados.
- La suite Karate de API ya se incorpora por separado. Ver KARATE.md. Este documento conserva las verificaciones manuales de interfaz y no implica que se hayan ejecutado.
