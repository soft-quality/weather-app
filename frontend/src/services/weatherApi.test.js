import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import {
  WeatherApiError,
  fetchCitySuggestions,
  fetchWeatherByCity,
  fetchWeatherByLocation,
} from './weatherApi'

function mockFetch(response) {
  const fn = vi.fn().mockResolvedValue(response)
  vi.stubGlobal('fetch', fn)
  return fn
}

const okResponse = (data) => ({ ok: true, status: 200, json: () => Promise.resolve(data) })
const errorResponse = (status, body) => ({
  ok: false,
  status,
  json: () => (body === undefined ? Promise.reject(new Error('no json')) : Promise.resolve(body)),
})

describe('weatherApi', () => {
  beforeEach(() => {
    vi.unstubAllGlobals()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  describe('WeatherApiError', () => {
    it('conserva mensaje, nombre y status', () => {
      const error = new WeatherApiError('boom', 502)
      expect(error).toBeInstanceOf(Error)
      expect(error.message).toBe('boom')
      expect(error.name).toBe('WeatherApiError')
      expect(error.status).toBe(502)
    })
  })

  describe('fetchWeatherByCity', () => {
    it('llama al endpoint con la ciudad codificada y devuelve el JSON', async () => {
      const data = { location: { name: 'São Paulo' } }
      const fetchMock = mockFetch(okResponse(data))

      const result = await fetchWeatherByCity('São Paulo')

      expect(result).toEqual(data)
      expect(fetchMock).toHaveBeenCalledTimes(1)
      const url = new URL(fetchMock.mock.calls[0][0])
      expect(url.pathname).toBe('/api/weather')
      expect(url.searchParams.get('city')).toBe('São Paulo')
    })

    it('lanza WeatherApiError con el mensaje del backend', async () => {
      mockFetch(errorResponse(404, { message: 'No se encontró la ciudad: X' }))

      await expect(fetchWeatherByCity('X')).rejects.toMatchObject({
        name: 'WeatherApiError',
        message: 'No se encontró la ciudad: X',
        status: 404,
      })
    })

    it('usa el mensaje por defecto si el cuerpo de error no es JSON', async () => {
      mockFetch(errorResponse(500))

      await expect(fetchWeatherByCity('X')).rejects.toMatchObject({
        message: 'No se pudo obtener el clima. Intenta nuevamente.',
        status: 500,
      })
    })

    it('usa el mensaje por defecto si el cuerpo no trae message', async () => {
      mockFetch(errorResponse(500, {}))

      await expect(fetchWeatherByCity('X')).rejects.toThrow('No se pudo obtener el clima. Intenta nuevamente.')
    })
  })

  describe('fetchWeatherByLocation', () => {
    it('envía coordenadas, nombre, admin1 y país', async () => {
      const fetchMock = mockFetch(okResponse({ ok: true }))

      await fetchWeatherByLocation({
        latitude: 6.24,
        longitude: -75.58,
        name: 'Medellín',
        admin1: 'Antioquia',
        country: 'Colombia',
      })

      const url = new URL(fetchMock.mock.calls[0][0])
      expect(url.pathname).toBe('/api/weather')
      expect(url.searchParams.get('lat')).toBe('6.24')
      expect(url.searchParams.get('lon')).toBe('-75.58')
      expect(url.searchParams.get('name')).toBe('Medellín')
      expect(url.searchParams.get('admin1')).toBe('Antioquia')
      expect(url.searchParams.get('country')).toBe('Colombia')
    })

    it('omite admin1 y country cuando no existen', async () => {
      const fetchMock = mockFetch(okResponse({}))

      await fetchWeatherByLocation({ latitude: 1, longitude: 2, name: 'X' })

      const url = new URL(fetchMock.mock.calls[0][0])
      expect(url.searchParams.has('admin1')).toBe(false)
      expect(url.searchParams.has('country')).toBe(false)
    })

    it('lanza WeatherApiError cuando la respuesta no es ok', async () => {
      mockFetch(errorResponse(502, { message: 'proveedor caído' }))

      await expect(
        fetchWeatherByLocation({ latitude: 1, longitude: 2, name: 'X' }),
      ).rejects.toMatchObject({ message: 'proveedor caído', status: 502 })
    })
  })

  describe('fetchCitySuggestions', () => {
    it('devuelve lista vacía sin llamar a la red si la consulta tiene menos de 2 caracteres', async () => {
      const fetchMock = mockFetch(okResponse([]))

      expect(await fetchCitySuggestions(' a ')).toEqual([])
      expect(fetchMock).not.toHaveBeenCalled()
    })

    it('consulta el endpoint con la consulta recortada y la señal de aborto', async () => {
      const suggestions = [{ name: 'Madrid' }]
      const fetchMock = mockFetch(okResponse(suggestions))
      const controller = new AbortController()

      const result = await fetchCitySuggestions('  Mad  ', { signal: controller.signal })

      expect(result).toEqual(suggestions)
      const url = new URL(fetchMock.mock.calls[0][0])
      expect(url.pathname).toBe('/api/cities')
      expect(url.searchParams.get('query')).toBe('Mad')
      expect(fetchMock.mock.calls[0][1]).toEqual({ signal: controller.signal })
    })

    it('limita la longitud de la consulta enviada', async () => {
      const fetchMock = mockFetch(okResponse([]))

      await fetchCitySuggestions('a'.repeat(500))

      const url = new URL(fetchMock.mock.calls[0][0])
      expect(url.searchParams.get('query')).toHaveLength(100)
    })

    it('funciona sin opciones', async () => {
      const fetchMock = mockFetch(okResponse([]))

      await fetchCitySuggestions('Lima')

      expect(fetchMock.mock.calls[0][1]).toEqual({ signal: undefined })
    })

    it('lanza error con mensaje por defecto cuando falla', async () => {
      mockFetch(errorResponse(500))

      await expect(fetchCitySuggestions('Lima')).rejects.toThrow('No se pudieron cargar las sugerencias.')
    })
  })
})
