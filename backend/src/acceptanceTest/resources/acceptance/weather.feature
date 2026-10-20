Feature: Aceptación de la API meteorológica
  El usuario obtiene el tiempo actual, el pronóstico y las sugerencias de ciudades.
  Los datos de Open-Meteo se sustituyen por respuestas HTTP locales controladas.

  Background:
    Given url baseUrl

  Scenario: PA-API-01 Consultar el tiempo actual de Bogotá
    Given path 'api', 'weather'
    And param city = 'Bogotá'
    When method get
    Then status 200
    And match response.location contains { name: 'Bogotá', country: 'Colombia', latitude: 4.6097, longitude: -74.0817 }
    And match response.current == { temperature: 18.5, apparentTemperature: 17.2, humidity: 70, windSpeed: 12.4, weatherCode: 3, isDay: true }

  Scenario: PA-API-02 Consultar el pronóstico diario
    Given path 'api', 'weather'
    And param city = 'Bogotá'
    When method get
    Then status 200
    And match response.daily == '#[2]'
    And match response.daily[0] == { date: '2026-10-19', tempMax: 20, tempMin: 10, weatherCode: 3 }
    And match response.daily[1] == { date: '2026-10-20', tempMax: 21, tempMin: 11, weatherCode: 61 }

  Scenario: PA-API-03 Consultar por las coordenadas de una sugerencia
    Given path 'api', 'weather'
    And params { lat: 4.6097, lon: -74.0817, name: 'Bogotá', admin1: 'Bogotá D.C.', country: 'Colombia' }
    When method get
    Then status 200
    And match response.location == { name: 'Bogotá', admin1: 'Bogotá D.C.', country: 'Colombia', latitude: 4.6097, longitude: -74.0817 }
    And match response.current.temperature == 18.5

  Scenario: PA-API-04 Obtener sugerencias de ciudades
    Given path 'api', 'cities'
    And param query = 'Bog'
    When method get
    Then status 200
    And match response == '#[1]'
    And match response[0] == { name: 'Bogotá', admin1: 'Bogotá D.C.', country: 'Colombia', latitude: 4.6097, longitude: -74.0817 }

  Scenario: PA-API-05 Informar una ciudad inexistente
    Given path 'api', 'weather'
    And param city = 'CiudadInexistente'
    When method get
    Then status 404
    And match response.status == 404
    And match response.message contains 'CiudadInexistente'

  Scenario: PA-API-06 No sugerir ciudades para una consulta vacía
    Given path 'api', 'cities'
    And param query = ''
    When method get
    Then status 200
    And match response == []

  Scenario: PA-API-07 Informar una respuesta meteorológica incompleta
    Given path 'api', 'weather'
    And params { lat: -1, lon: 0, name: 'Proveedor incompleto' }
    When method get
    Then status 502
    And match response.status == 502
    And match response.message contains 'No se pudo obtener el clima'
