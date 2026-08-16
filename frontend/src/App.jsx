import { useState } from 'react';
import SearchBar from './components/SearchBar';
import WeatherCard from './components/WeatherCard';
import ForecastList from './components/ForecastList';
import Loader from './components/Loader';
import ErrorMessage from './components/ErrorMessage';
import { fetchWeatherByCity } from './services/weatherApi';
import './App.css';

export default function App() {
  const [weather, setWeather] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  async function handleSearch(city) {
    setIsLoading(true);
    setError(null);

    try {
      const data = await fetchWeatherByCity(city);
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
        <h1 className="app__title">Weather App</h1>
        <p className="app__subtitle">Consulta el clima actual y el pronóstico de cualquier ciudad</p>
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
