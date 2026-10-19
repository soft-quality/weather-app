import { describe, expect, it } from 'vitest';
import { summarizeCurrentWeather, summarizeForecastWeather } from './sonarDuplicationDemo';

describe('demostración académica de duplicación', () => {
  it.each([
    ['clima actual', summarizeCurrentWeather],
    ['pronóstico', summarizeForecastWeather],
  ])('convierte y redondea las mediciones de %s sin modificar la entrada', (_name, summarize) => {
    const reading = {
      temperature: '23.46', feelsLike: '24.14', humidity: '75',
      pressure: '1013', windSpeed: '12.34', windDirection: '180',
      visibility: '9.56', cloudCover: '40', precipitation: '1.26', uvIndex: '3.24',
    };
    const original = { ...reading };
    expect(summarize(reading)).toBe('23.5 | 24.1 | 75.0 | 1013.0 | 12.3 | 180.0 | 9.6 | 40.0 | 1.3 | 3.2');
    expect(reading).toEqual(original);
  });
});
