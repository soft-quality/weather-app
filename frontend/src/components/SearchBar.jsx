import { useEffect, useId, useRef, useState } from 'react';
import { fetchCitySuggestions } from '../services/weatherApi';
import './SearchBar.css';

function formatLocationLabel(location) {
  return [location.name, location.admin1, location.country].filter(Boolean).join(', ');
}

export default function SearchBar({ onSearch, isLoading }) {
  const [value, setValue] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);
  const [selectedLabel, setSelectedLabel] = useState(null);

  const containerRef = useRef(null);
  const abortRef = useRef(null);
  const listboxId = useId();

  useEffect(() => {
    const trimmed = value.trim();

    if (trimmed.length < 2 || trimmed === selectedLabel) {
      setSuggestions([]);
      setIsOpen(false);
      return undefined;
    }

    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;

    const timeoutId = setTimeout(async () => {
      try {
        const results = await fetchCitySuggestions(trimmed, { signal: controller.signal });
        setSuggestions(results);
        setIsOpen(results.length > 0);
        setActiveIndex(-1);
      } catch (err) {
        if (err.name !== 'AbortError') {
          setSuggestions([]);
          setIsOpen(false);
        }
      }
    }, 250);

    return () => {
      clearTimeout(timeoutId);
      controller.abort();
    };
  }, [value, selectedLabel]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  function selectSuggestion(location) {
    const label = formatLocationLabel(location);
    setValue(label);
    setSelectedLabel(label);
    setSuggestions([]);
    setIsOpen(false);
    setActiveIndex(-1);
    onSearch(location);
  }

  function handleChange(event) {
    setValue(event.target.value);
    setSelectedLabel(null);
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (activeIndex >= 0 && suggestions[activeIndex]) {
      selectSuggestion(suggestions[activeIndex]);
      return;
    }
    const trimmed = value.trim();
    if (trimmed.length === 0) {
      return;
    }
    setIsOpen(false);
    onSearch(trimmed);
  }

  function handleKeyDown(event) {
    if (!isOpen || suggestions.length === 0) {
      return;
    }

    if (event.key === 'ArrowDown') {
      event.preventDefault();
      setActiveIndex((index) => (index + 1) % suggestions.length);
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      setActiveIndex((index) => (index <= 0 ? suggestions.length - 1 : index - 1));
    } else if (event.key === 'Escape') {
      setIsOpen(false);
      setActiveIndex(-1);
    }
  }

  return (
    <div className="search-bar" ref={containerRef}>
      <form className="search-bar__form" onSubmit={handleSubmit} autoComplete="off">
        <label htmlFor="city-input" className="search-bar__label">
          Buscar ciudad
        </label>
        <div
          className="search-bar__combobox"
          role="combobox"
          aria-expanded={isOpen}
          aria-owns={listboxId}
          aria-haspopup="listbox"
        >
          <svg className="search-bar__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" aria-hidden="true">
            <circle cx="11" cy="11" r="7" />
            <path d="m20 20-3.2-3.2" />
          </svg>
          <input
            id="city-input"
            className="search-bar__input"
            type="text"
            placeholder="Ej: Bogotá, Madrid, Buenos Aires"
            value={value}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
            onFocus={() => suggestions.length > 0 && setIsOpen(true)}
            autoComplete="off"
            role="searchbox"
            aria-autocomplete="list"
            aria-controls={listboxId}
            aria-activedescendant={activeIndex >= 0 ? `${listboxId}-${activeIndex}` : undefined}
          />
          <button
            className="search-bar__submit"
            type="submit"
            disabled={isLoading}
            aria-label="Buscar clima"
          >
            {isLoading ? 'Buscando…' : 'Buscar'}
          </button>
        </div>

        {isOpen && (
          <ul className="search-bar__listbox" role="listbox" id={listboxId}>
            {suggestions.map((location, index) => (
              <li
                key={`${location.name}-${location.latitude}-${location.longitude}`}
                id={`${listboxId}-${index}`}
                role="option"
                aria-selected={index === activeIndex}
                className={`search-bar__option${index === activeIndex ? ' is-active' : ''}`}
                onMouseDown={(event) => {
                  event.preventDefault();
                  selectSuggestion(location);
                }}
                onMouseEnter={() => setActiveIndex(index)}
              >
                <span className="search-bar__option-name">{location.name}</span>
                <span className="search-bar__option-meta">
                  {[location.admin1, location.country].filter(Boolean).join(', ')}
                </span>
              </li>
            ))}
          </ul>
        )}
      </form>
    </div>
  );
}
