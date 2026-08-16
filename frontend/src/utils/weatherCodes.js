// Mapeo de codigos WMO (usados por Open-Meteo) a descripcion e icono.
// https://open-meteo.com/en/docs (seccion "WMO Weather interpretation codes")
const WEATHER_CODES = {
  0: { label: 'Cielo despejado', icon: 'sun' },
  1: { label: 'Mayormente despejado', icon: 'sun' },
  2: { label: 'Parcialmente nublado', icon: 'cloud-sun' },
  3: { label: 'Nublado', icon: 'cloud' },
  45: { label: 'Niebla', icon: 'fog' },
  48: { label: 'Niebla con escarcha', icon: 'fog' },
  51: { label: 'Llovizna ligera', icon: 'drizzle' },
  53: { label: 'Llovizna moderada', icon: 'drizzle' },
  55: { label: 'Llovizna intensa', icon: 'drizzle' },
  56: { label: 'Llovizna helada ligera', icon: 'drizzle' },
  57: { label: 'Llovizna helada intensa', icon: 'drizzle' },
  61: { label: 'Lluvia ligera', icon: 'rain' },
  63: { label: 'Lluvia moderada', icon: 'rain' },
  65: { label: 'Lluvia intensa', icon: 'rain' },
  66: { label: 'Lluvia helada ligera', icon: 'rain' },
  67: { label: 'Lluvia helada intensa', icon: 'rain' },
  71: { label: 'Nevada ligera', icon: 'snow' },
  73: { label: 'Nevada moderada', icon: 'snow' },
  75: { label: 'Nevada intensa', icon: 'snow' },
  77: { label: 'Granos de nieve', icon: 'snow' },
  80: { label: 'Chubascos ligeros', icon: 'rain' },
  81: { label: 'Chubascos moderados', icon: 'rain' },
  82: { label: 'Chubascos violentos', icon: 'rain' },
  85: { label: 'Chubascos de nieve ligeros', icon: 'snow' },
  86: { label: 'Chubascos de nieve intensos', icon: 'snow' },
  95: { label: 'Tormenta electrica', icon: 'storm' },
  96: { label: 'Tormenta con granizo ligero', icon: 'storm' },
  99: { label: 'Tormenta con granizo intenso', icon: 'storm' },
};

const DEFAULT_WEATHER = { label: 'Condicion desconocida', icon: 'cloud' };

export function describeWeatherCode(code) {
  return WEATHER_CODES[code] ?? DEFAULT_WEATHER;
}

export function formatDayLabel(dateString) {
  const date = new Date(`${dateString}T00:00:00`);
  return date.toLocaleDateString('es-ES', { weekday: 'short', day: 'numeric', month: 'short' });
}
