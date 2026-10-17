const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export class WeatherApiError extends Error {
  constructor(message, status) {
    super(message);
    this.name = 'WeatherApiError';
    this.status = status;
  }
}

const MAX_QUERY_LENGTH = 100;

// Construye la URL con un origen fijo; el texto del usuario viaja solo como parámetro de consulta.
function buildApiUrl(path, params) {
  const url = new URL(path, API_BASE_URL);
  Object.entries(params).forEach(([key, value]) => url.searchParams.set(key, value));
  return url;
}

function sanitizeQuery(text) {
  return String(text).trim().slice(0, MAX_QUERY_LENGTH);
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
  const url = buildApiUrl('/api/weather', { city: sanitizeQuery(city) });
  const response = await fetch(url);
  return parseOrThrow(response, 'No se pudo obtener el clima. Intenta nuevamente.');
}

export async function fetchWeatherByLocation(location) {
  const params = {
    lat: String(location.latitude),
    lon: String(location.longitude),
    name: location.name,
  };
  if (location.admin1) params.admin1 = location.admin1;
  if (location.country) params.country = location.country;

  const url = buildApiUrl('/api/weather', params);
  const response = await fetch(url);
  return parseOrThrow(response, 'No se pudo obtener el clima. Intenta nuevamente.');
}

export async function fetchCitySuggestions(query, { signal } = {}) {
  const trimmed = sanitizeQuery(query);
  if (trimmed.length < 2) {
    return [];
  }

  const url = buildApiUrl('/api/cities', { query: trimmed });
  const response = await fetch(url, { signal });
  return parseOrThrow(response, 'No se pudieron cargar las sugerencias.');
}
