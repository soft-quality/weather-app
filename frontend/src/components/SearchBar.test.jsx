import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { act, fireEvent, render, screen } from '@testing-library/react'
import SearchBar from './SearchBar'
import { fetchCitySuggestions } from '../services/weatherApi'

vi.mock('../services/weatherApi', () => ({
  fetchCitySuggestions: vi.fn(),
}))

const MEDELLIN = { name: 'Medellín', admin1: 'Antioquia', country: 'Colombia', latitude: 6.24, longitude: -75.58 }
const MEDINA = { name: 'Medina', admin1: null, country: 'Arabia Saudita', latitude: 24.4, longitude: 39.6 }

async function typeAndWait(input, text) {
  fireEvent.change(input, { target: { value: text } })
  await act(async () => {
    await vi.advanceTimersByTimeAsync(300)
  })
}

describe('SearchBar', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    fetchCitySuggestions.mockReset()
    fetchCitySuggestions.mockResolvedValue([MEDELLIN, MEDINA])
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('renderiza el campo y el botón', () => {
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)
    expect(screen.getByRole('searchbox')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Buscar clima' })).toHaveTextContent('Buscar')
  })

  it('deshabilita el botón y muestra "Buscando…" mientras carga', () => {
    render(<SearchBar onSearch={vi.fn()} isLoading />)
    const button = screen.getByRole('button', { name: 'Buscar clima' })
    expect(button).toBeDisabled()
    expect(button).toHaveTextContent('Buscando…')
  })

  it('envía el texto recortado al enviar el formulario', () => {
    const onSearch = vi.fn()
    render(<SearchBar onSearch={onSearch} isLoading={false} />)

    fireEvent.change(screen.getByRole('searchbox'), { target: { value: '  Bogotá  ' } })
    fireEvent.submit(screen.getByRole('searchbox').closest('form'))

    expect(onSearch).toHaveBeenCalledWith('Bogotá')
  })

  it('no busca si el campo está vacío', () => {
    const onSearch = vi.fn()
    render(<SearchBar onSearch={onSearch} isLoading={false} />)

    fireEvent.submit(screen.getByRole('searchbox').closest('form'))

    expect(onSearch).not.toHaveBeenCalled()
  })

  it('no consulta sugerencias con menos de 2 caracteres', async () => {
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)

    await typeAndWait(screen.getByRole('searchbox'), 'a')

    expect(fetchCitySuggestions).not.toHaveBeenCalled()
    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()
  })

  it('muestra sugerencias tras el debounce', async () => {
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)

    await typeAndWait(screen.getByRole('searchbox'), 'Med')

    expect(fetchCitySuggestions).toHaveBeenCalledWith('Med', expect.objectContaining({ signal: expect.anything() }))
    expect(screen.getByRole('listbox')).toBeInTheDocument()
    expect(screen.getAllByRole('option')).toHaveLength(2)
    expect(screen.getByText('Antioquia, Colombia')).toBeInTheDocument()
    expect(screen.getByText('Arabia Saudita')).toBeInTheDocument()
  })

  it('no abre la lista si no hay resultados', async () => {
    fetchCitySuggestions.mockResolvedValue([])
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)

    await typeAndWait(screen.getByRole('searchbox'), 'zzz')

    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()
  })

  it('oculta sugerencias cuando falla la consulta', async () => {
    fetchCitySuggestions.mockRejectedValue(new Error('network'))
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)

    await typeAndWait(screen.getByRole('searchbox'), 'Med')

    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()
  })

  it('ignora errores de aborto sin alterar el estado', async () => {
    const abortError = new Error('aborted')
    abortError.name = 'AbortError'
    fetchCitySuggestions.mockRejectedValue(abortError)
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)

    await typeAndWait(screen.getByRole('searchbox'), 'Med')

    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()
  })

  it('selecciona una sugerencia con el mouse y dispara onSearch', async () => {
    const onSearch = vi.fn()
    render(<SearchBar onSearch={onSearch} isLoading={false} />)
    const input = screen.getByRole('searchbox')
    await typeAndWait(input, 'Med')

    fireEvent.mouseEnter(screen.getAllByRole('option')[0])
    expect(screen.getAllByRole('option')[0]).toHaveAttribute('aria-selected', 'true')
    fireEvent.mouseDown(screen.getAllByRole('option')[0])

    expect(onSearch).toHaveBeenCalledWith(MEDELLIN)
    expect(input).toHaveValue('Medellín, Antioquia, Colombia')
    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()

    // el valor seleccionado no vuelve a disparar sugerencias
    fetchCitySuggestions.mockClear()
    await act(async () => {
      await vi.advanceTimersByTimeAsync(300)
    })
    expect(fetchCitySuggestions).not.toHaveBeenCalled()
  })

  it('navega con flechas y selecciona con Enter', async () => {
    const onSearch = vi.fn()
    render(<SearchBar onSearch={onSearch} isLoading={false} />)
    const input = screen.getByRole('searchbox')
    await typeAndWait(input, 'Med')

    fireEvent.keyDown(input, { key: 'ArrowDown' })
    expect(screen.getAllByRole('option')[0]).toHaveAttribute('aria-selected', 'true')
    fireEvent.keyDown(input, { key: 'ArrowDown' })
    expect(screen.getAllByRole('option')[1]).toHaveAttribute('aria-selected', 'true')
    fireEvent.keyDown(input, { key: 'ArrowDown' })
    expect(screen.getAllByRole('option')[0]).toHaveAttribute('aria-selected', 'true')
    fireEvent.keyDown(input, { key: 'ArrowUp' })
    expect(screen.getAllByRole('option')[1]).toHaveAttribute('aria-selected', 'true')
    fireEvent.keyDown(input, { key: 'ArrowUp' })
    fireEvent.keyDown(input, { key: 'ArrowUp' })
    expect(screen.getAllByRole('option')[1]).toHaveAttribute('aria-selected', 'true')

    fireEvent.submit(input.closest('form'))

    expect(onSearch).toHaveBeenCalledWith(MEDINA)
  })

  it('cierra la lista con Escape', async () => {
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)
    const input = screen.getByRole('searchbox')
    await typeAndWait(input, 'Med')

    fireEvent.keyDown(input, { key: 'Escape' })

    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()
  })

  it('ignora otras teclas y teclas sin lista abierta', async () => {
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)
    const input = screen.getByRole('searchbox')

    fireEvent.keyDown(input, { key: 'ArrowDown' })
    await typeAndWait(input, 'Med')
    fireEvent.keyDown(input, { key: 'a' })

    expect(screen.getByRole('listbox')).toBeInTheDocument()
  })

  it('cierra la lista al hacer clic fuera y la reabre al enfocar', async () => {
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)
    const input = screen.getByRole('searchbox')
    await typeAndWait(input, 'Med')

    fireEvent.mouseDown(document.body)
    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()

    fireEvent.focus(input)
    expect(screen.getByRole('listbox')).toBeInTheDocument()

    // clic dentro no cierra
    fireEvent.mouseDown(input)
    expect(screen.getByRole('listbox')).toBeInTheDocument()
  })

  it('no reabre al enfocar si no hay sugerencias', () => {
    render(<SearchBar onSearch={vi.fn()} isLoading={false} />)

    fireEvent.focus(screen.getByRole('searchbox'))

    expect(screen.queryByRole('listbox')).not.toBeInTheDocument()
  })
})
