import { useState } from 'react';
import './SearchBar.css';

export default function SearchBar({ onSearch, isLoading }) {
  const [value, setValue] = useState('');

  function handleSubmit(event) {
    event.preventDefault();
    const trimmed = value.trim();
    if (trimmed.length === 0) {
      return;
    }
    onSearch(trimmed);
  }

  return (
    <form className="search-bar glass" onSubmit={handleSubmit}>
      <label htmlFor="city-input" className="search-bar__label">
        Ciudad
      </label>
      <div className="search-bar__row">
        <input
          id="city-input"
          className="search-bar__input"
          type="text"
          placeholder="Ej: Bogota, Madrid, Buenos Aires"
          value={value}
          onChange={(event) => setValue(event.target.value)}
          autoComplete="off"
        />
        <button
          className="search-bar__button"
          type="submit"
          disabled={isLoading}
          aria-label="Buscar clima"
        >
          {isLoading ? 'Buscando…' : 'Buscar'}
        </button>
      </div>
    </form>
  );
}
