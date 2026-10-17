import { describe, expect, it } from 'vitest'
import { describeWeatherCode, formatDayLabel } from './weatherCodes'

describe('describeWeatherCode', () => {
  it.each([
    [0, 'Cielo despejado', 'sun'],
    [2, 'Parcialmente nublado', 'cloud-sun'],
    [3, 'Nublado', 'cloud'],
    [45, 'Niebla', 'fog'],
    [53, 'Llovizna moderada', 'drizzle'],
    [63, 'Lluvia moderada', 'rain'],
    [75, 'Nevada intensa', 'snow'],
    [95, 'Tormenta electrica', 'storm'],
  ])('describe el código %i', (code, label, icon) => {
    expect(describeWeatherCode(code)).toEqual({ label, icon })
  })

  it('devuelve una condición por defecto para códigos desconocidos', () => {
    expect(describeWeatherCode(12345)).toEqual({ label: 'Condicion desconocida', icon: 'cloud' })
  })

  it('devuelve la condición por defecto para undefined', () => {
    expect(describeWeatherCode(undefined).icon).toBe('cloud')
  })
})

describe('formatDayLabel', () => {
  it('formatea la fecha en español con día de la semana', () => {
    const label = formatDayLabel('2026-01-05')
    expect(label).toMatch(/lun/i)
    expect(label).toMatch(/5/)
    expect(label).toMatch(/ene/i)
  })
})
