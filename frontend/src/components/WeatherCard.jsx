import WeatherIcon from './WeatherIcon';
import { describeWeatherCode } from '../utils/weatherCodes';
import './WeatherCard.css';

export default function WeatherCard({ weather }) {
  const { location, current } = weather;
  const condition = describeWeatherCode(current.weatherCode);
  const icon = condition.icon === 'sun' && !current.isDay ? 'moon' : condition.icon;

  return (
    <section className="weather-card glass" aria-label={`Clima actual en ${location.name}`}>
      <header className="weather-card__location">
        <h2>{location.name}</h2>
        <span>{location.country}</span>
      </header>

      <div className="weather-card__main">
        <WeatherIcon icon={icon} className="weather-card__icon" size={64} />
        <span className="weather-card__temp">{Math.round(current.temperature)}°</span>
      </div>

      <p className="weather-card__condition">{condition.label}</p>

      <dl className="weather-card__details">
        <div>
          <dt>Sensación</dt>
          <dd>{Math.round(current.apparentTemperature)}°</dd>
        </div>
        <div>
          <dt>Humedad</dt>
          <dd>{current.humidity}%</dd>
        </div>
        <div>
          <dt>Viento</dt>
          <dd>{Math.round(current.windSpeed)} km/h</dd>
        </div>
      </dl>
    </section>
  );
}
