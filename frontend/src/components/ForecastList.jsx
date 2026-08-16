import WeatherIcon from './WeatherIcon';
import { describeWeatherCode, formatDayLabel } from '../utils/weatherCodes';
import './ForecastList.css';

export default function ForecastList({ daily }) {
  if (!daily || daily.length === 0) {
    return null;
  }

  return (
    <section className="forecast-list glass" aria-label="Pronostico de los proximos dias">
      <h3 className="forecast-list__title">Proximos dias</h3>
      <ul className="forecast-list__items">
        {daily.map((day) => {
          const condition = describeWeatherCode(day.weatherCode);
          return (
            <li key={day.date} className="forecast-list__item">
              <span className="forecast-list__day">{formatDayLabel(day.date)}</span>
              <WeatherIcon icon={condition.icon} size={28} className="forecast-list__icon" />
              <span className="forecast-list__temps">
                <strong>{Math.round(day.tempMax)}°</strong>
                <span className="forecast-list__temp-min">{Math.round(day.tempMin)}°</span>
              </span>
            </li>
          );
        })}
      </ul>
    </section>
  );
}
