import { describe, expect, it } from 'vitest'
import { render, screen } from '@testing-library/react'
import ErrorMessage from './ErrorMessage'
import Loader from './Loader'
import WeatherIcon from './WeatherIcon'
import ForecastList from './ForecastList'
import WeatherCard from './WeatherCard'

describe('ErrorMessage', () => {
  it('muestra el mensaje con rol alert', () => {
    render(<ErrorMessage message="Algo salió mal" />)
    expect(screen.getByRole('alert')).toHaveTextContent('Algo salió mal')
  })
})

describe('Loader', () => {
  it('muestra el estado de carga', () => {
    render(<Loader />)
    expect(screen.getByRole('status')).toHaveTextContent('Consultando el clima')
  })
})

describe('WeatherIcon', () => {
  it.each(['sun', 'moon', 'cloud-sun', 'cloud', 'fog', 'drizzle', 'rain', 'snow', 'storm'])(
    'renderiza el icono %s',
    (icon) => {
      const { container } = render(<WeatherIcon icon={icon} size={30} className="x" />)
      const svg = container.querySelector('svg')
      expect(svg).toHaveAttribute('width', '30')
      expect(svg).toHaveClass('x')
      expect(svg.children.length).toBeGreaterThan(0)
    },
  )

  it('usa nube y tamaño 48 por defecto para iconos desconocidos', () => {
    const { container } = render(<WeatherIcon icon="nope" />)
    const cloud = render(<WeatherIcon icon="cloud" />).container.querySelector('svg')
    const svg = container.querySelector('svg')
    expect(svg).toHaveAttribute('width', '48')
    expect(svg.innerHTML).toBe(cloud.innerHTML)
  })
})

describe('ForecastList', () => {
  it('no renderiza nada sin datos', () => {
    const { container, rerender } = render(<ForecastList daily={[]} />)
    expect(container).toBeEmptyDOMElement()
    rerender(<ForecastList daily={null} />)
    expect(container).toBeEmptyDOMElement()
    rerender(<ForecastList />)
    expect(container).toBeEmptyDOMElement()
  })

  it('lista cada día con temperaturas redondeadas', () => {
    const daily = [
      { date: '2026-01-05', tempMax: 25.6, tempMin: 14.2, weatherCode: 0 },
      { date: '2026-01-06', tempMax: 20.4, tempMin: 10.5, weatherCode: 61 },
    ]
    render(<ForecastList daily={daily} />)

    expect(screen.getByText('Próximos días')).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
    expect(screen.getByText('26°')).toBeInTheDocument()
    expect(screen.getByText('14°')).toBeInTheDocument()
    expect(screen.getByText('20°')).toBeInTheDocument()
  })
})

describe('WeatherCard', () => {
  const base = {
    location: { name: 'Medellín', admin1: 'Antioquia', country: 'Colombia' },
    current: {
      temperature: 22.6,
      apparentTemperature: 23.4,
      humidity: 61,
      windSpeed: 10.7,
      weatherCode: 3,
      isDay: true,
    },
  }

  it('muestra ubicación, temperatura y detalles', () => {
    render(<WeatherCard weather={base} />)

    expect(screen.getByRole('heading', { name: 'Medellín' })).toBeInTheDocument()
    expect(screen.getByText('Antioquia, Colombia')).toBeInTheDocument()
    expect(screen.getByText('23°', { selector: '.weather-card__temp' })).toBeInTheDocument()
    expect(screen.getByText('Nublado')).toBeInTheDocument()
    expect(screen.getByText('61%')).toBeInTheDocument()
    expect(screen.getByText('11 km/h')).toBeInTheDocument()
    expect(screen.getByLabelText('Clima actual en Medellín')).toBeInTheDocument()
  })

  it('omite admin1/país ausentes', () => {
    const weather = { ...base, location: { name: 'Lima', country: 'Perú' } }
    render(<WeatherCard weather={weather} />)
    expect(screen.getByText('Perú')).toBeInTheDocument()
  })

  it('usa el icono de luna cuando está despejado de noche', () => {
    const night = { ...base, current: { ...base.current, weatherCode: 0, isDay: false } }
    const day = { ...base, current: { ...base.current, weatherCode: 0, isDay: true } }

    const nightSvg = render(<WeatherCard weather={night} />).container.querySelector('.weather-card__icon')
    const daySvg = render(<WeatherCard weather={day} />).container.querySelector('.weather-card__icon')

    expect(nightSvg.innerHTML).not.toBe(daySvg.innerHTML)
  })

  it('no cambia a luna con cielo nublado de noche', () => {
    const cloudy = { ...base, current: { ...base.current, weatherCode: 3, isDay: false } }
    const cloudyDay = { ...base, current: { ...base.current, weatherCode: 3, isDay: true } }

    const a = render(<WeatherCard weather={cloudy} />).container.querySelector('.weather-card__icon')
    const b = render(<WeatherCard weather={cloudyDay} />).container.querySelector('.weather-card__icon')

    expect(a.innerHTML).toBe(b.innerHTML)
  })
})
