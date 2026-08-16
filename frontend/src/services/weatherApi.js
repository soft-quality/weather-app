const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export class WeatherApiError extends Error {
  constructor(message, status) {
    super(message);
    this.name = 'WeatherApiError';
    this.status = status;
  }
}

export async function fetchWeatherByCity(city) {
  const url = `${API_BASE_URL}/api/weather?city=${encodeURIComponent(city)}`;
  const response = await fetch(url);

  if (!response.ok) {
    const body = await response.json().catch(() => null);
    const message = body?.message ?? 'No se pudo obtener el clima. Intenta nuevamente.';
    throw new WeatherApiError(message, response.status);
  }

  return response.json();
}
