import { useState } from 'react';
import SearchBar from './components/SearchBar';
import WeatherCard from './components/WeatherCard';
import ForecastList from './components/ForecastList';
import Loader from './components/Loader';
import ErrorMessage from './components/ErrorMessage';
import { fetchWeatherByCity, fetchWeatherByLocation } from './services/weatherApi';
import './App.css';

export default function App() {
  const [weather, setWeather] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  async function handleSearch(query) {
    setIsLoading(true);
    setError(null);

    try {
      const data = typeof query === 'string' ? await fetchWeatherByCity(query) : await fetchWeatherByLocation(query);
      setWeather(data);
    } catch (err) {
      setWeather(null);
      setError(err.message ?? 'Ocurrió un error inesperado.');
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="app">
      <header className="app__header">
        <span className="app__eyebrow">Estación meteorológica</span>
        <h1 className="app__title">Clima</h1>
      </header>

      <main className="app__content">
        <SearchBar onSearch={handleSearch} isLoading={isLoading} />

        {isLoading && <Loader />}
        {!isLoading && error && <ErrorMessage message={error} />}
        {!isLoading && !error && weather && (
          <>
            <WeatherCard weather={weather} />
            <ForecastList daily={weather.daily} />
          </>
        )}
      </main>

      <footer className="app__footer">
        Datos meteorológicos de{' '}
        <a href="https://open-meteo.com/" target="_blank" rel="noreferrer">
          Open-Meteo.com
        </a>
      </footer>
    </div>
  );
}
