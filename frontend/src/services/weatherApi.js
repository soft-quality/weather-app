const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export class WeatherApiError extends Error {
  constructor(message, status) {
    super(message);
    this.name = 'WeatherApiError';
    this.status = status;
  }
}

async function parseOrThrow(response, fallbackMessage) {
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    const message = body?.message ?? fallbackMessage;
    throw new WeatherApiError(message, response.status);
  }
  return response.json();
}

export async function fetchWeatherByCity(city) {
  const url = `${API_BASE_URL}/api/weather?city=${encodeURIComponent(city)}`;
  const response = await fetch(url);
  return parseOrThrow(response, 'No se pudo obtener el clima. Intenta nuevamente.');
}

export async function fetchWeatherByLocation(location) {
  const params = new URLSearchParams({
    lat: String(location.latitude),
    lon: String(location.longitude),
    name: location.name,
  });
  if (location.admin1) params.set('admin1', location.admin1);
  if (location.country) params.set('country', location.country);

  const url = `${API_BASE_URL}/api/weather?${params.toString()}`;
  const response = await fetch(url);
  return parseOrThrow(response, 'No se pudo obtener el clima. Intenta nuevamente.');
}

export async function fetchCitySuggestions(query, { signal } = {}) {
  const trimmed = query.trim();
  if (trimmed.length < 2) {
    return [];
  }

  const url = `${API_BASE_URL}/api/cities?query=${encodeURIComponent(trimmed)}`;
  const response = await fetch(url, { signal });
  return parseOrThrow(response, 'No se pudieron cargar las sugerencias.');
}
