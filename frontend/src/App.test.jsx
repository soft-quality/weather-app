import { beforeEach, describe, expect, it, vi } from 'vitest'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import App from './App'
import { fetchCitySuggestions, fetchWeatherByCity, fetchWeatherByLocation } from './services/weatherApi'

vi.mock('./services/weatherApi', () => ({
  fetchWeatherByCity: vi.fn(),
  fetchWeatherByLocation: vi.fn(),
  fetchCitySuggestions: vi.fn(),
}))

const WEATHER = {
  location: { name: 'Bogotá', admin1: 'Cundinamarca', country: 'Colombia', latitude: 4.6, longitude: -74.1 },
  current: { temperature: 14.2, apparentTemperature: 13, humidity: 80, windSpeed: 5, weatherCode: 3, isDay: true },
  daily: [{ date: '2026-01-05', tempMax: 18, tempMin: 9, weatherCode: 61 }],
}

function search(text) {
  fireEvent.change(screen.getByRole('searchbox'), { target: { value: text } })
  fireEvent.submit(screen.getByRole('searchbox').closest('form'))
}

describe('App', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    fetchCitySuggestions.mockResolvedValue([])
  })

  it('muestra el encabezado y el crédito de Open-Meteo', () => {
    render(<App />)
    expect(screen.getByRole('heading', { level: 1, name: 'Clima' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Open-Meteo.com' })).toHaveAttribute('href', 'https://open-meteo.com/')
  })

  it('busca por ciudad y muestra el clima y el pronóstico', async () => {
    fetchWeatherByCity.mockResolvedValue(WEATHER)
    render(<App />)

    search('Bogotá')

    expect(await screen.findByRole('heading', { name: 'Bogotá' })).toBeInTheDocument()
    expect(fetchWeatherByCity).toHaveBeenCalledWith('Bogotá')
    expect(screen.getByText('Próximos días')).toBeInTheDocument()
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  it('muestra el loader mientras la petición está pendiente', async () => {
    let resolve
    fetchWeatherByCity.mockReturnValue(new Promise((r) => { resolve = r }))
    render(<App />)

    search('Bogotá')

    expect(await screen.findByRole('status')).toHaveTextContent('Consultando el clima')
    resolve(WEATHER)
    expect(await screen.findByRole('heading', { name: 'Bogotá' })).toBeInTheDocument()
  })

  it('muestra el mensaje de error cuando la API falla', async () => {
    fetchWeatherByCity.mockRejectedValue(new Error('No se encontró la ciudad: Xyz'))
    render(<App />)

    search('Xyz')

    expect(await screen.findByRole('alert')).toHaveTextContent('No se encontró la ciudad: Xyz')
    expect(screen.queryByText('Próximos días')).not.toBeInTheDocument()
  })

  it('usa un mensaje genérico si el error no trae mensaje', async () => {
    fetchWeatherByCity.mockRejectedValue({})
    render(<App />)

    search('Xyz')

    expect(await screen.findByRole('alert')).toHaveTextContent('Ocurrió un error inesperado.')
  })

  it('busca por ubicación al elegir una sugerencia', async () => {
    const suggestion = { name: 'Bogotá', admin1: 'Cundinamarca', country: 'Colombia', latitude: 4.6, longitude: -74.1 }
    fetchCitySuggestions.mockResolvedValue([suggestion])
    fetchWeatherByLocation.mockResolvedValue(WEATHER)
    render(<App />)

    fireEvent.change(screen.getByRole('searchbox'), { target: { value: 'Bog' } })
    const option = await screen.findByRole('option')
    fireEvent.mouseDown(option)

    await waitFor(() => expect(fetchWeatherByLocation).toHaveBeenCalledWith(suggestion))
    expect(await screen.findByRole('heading', { name: 'Bogotá' })).toBeInTheDocument()
  })

  it('limpia el error previo en una búsqueda exitosa', async () => {
    fetchWeatherByCity.mockRejectedValueOnce(new Error('falló')).mockResolvedValueOnce(WEATHER)
    render(<App />)

    search('A1')
    expect(await screen.findByRole('alert')).toBeInTheDocument()

    search('Bogotá')
    expect(await screen.findByRole('heading', { name: 'Bogotá' })).toBeInTheDocument()
    expect(screen.queryByRole('alert')).not.toBeInTheDocument()
  })
})
