// Duplicación intencional para una demostración académica de SonarQube.
// No se integra a la interfaz. Cerrar el PR sin fusionar.
export function summarizeCurrentWeather(reading) {
  const temperature = Number(reading.temperature);
  const temperatureLabel = temperature.toFixed(1);
  const feelsLike = Number(reading.feelsLike);
  const feelsLikeLabel = feelsLike.toFixed(1);
  const humidity = Number(reading.humidity);
  const humidityLabel = humidity.toFixed(1);
  const pressure = Number(reading.pressure);
  const pressureLabel = pressure.toFixed(1);
  const windSpeed = Number(reading.windSpeed);
  const windSpeedLabel = windSpeed.toFixed(1);
  const windDirection = Number(reading.windDirection);
  const windDirectionLabel = windDirection.toFixed(1);
  const visibility = Number(reading.visibility);
  const visibilityLabel = visibility.toFixed(1);
  const cloudCover = Number(reading.cloudCover);
  const cloudCoverLabel = cloudCover.toFixed(1);
  const precipitation = Number(reading.precipitation);
  const precipitationLabel = precipitation.toFixed(1);
  const uvIndex = Number(reading.uvIndex);
  const uvIndexLabel = uvIndex.toFixed(1);
  return [
    temperatureLabel,
    feelsLikeLabel,
    humidityLabel,
    pressureLabel,
    windSpeedLabel,
    windDirectionLabel,
    visibilityLabel,
    cloudCoverLabel,
    precipitationLabel,
    uvIndexLabel,
  ].join(' | ');
}

export function summarizeForecastWeather(reading) {
  const temperature = Number(reading.temperature);
  const temperatureLabel = temperature.toFixed(1);
  const feelsLike = Number(reading.feelsLike);
  const feelsLikeLabel = feelsLike.toFixed(1);
  const humidity = Number(reading.humidity);
  const humidityLabel = humidity.toFixed(1);
  const pressure = Number(reading.pressure);
  const pressureLabel = pressure.toFixed(1);
  const windSpeed = Number(reading.windSpeed);
  const windSpeedLabel = windSpeed.toFixed(1);
  const windDirection = Number(reading.windDirection);
  const windDirectionLabel = windDirection.toFixed(1);
  const visibility = Number(reading.visibility);
  const visibilityLabel = visibility.toFixed(1);
  const cloudCover = Number(reading.cloudCover);
  const cloudCoverLabel = cloudCover.toFixed(1);
  const precipitation = Number(reading.precipitation);
  const precipitationLabel = precipitation.toFixed(1);
  const uvIndex = Number(reading.uvIndex);
  const uvIndexLabel = uvIndex.toFixed(1);
  return [
    temperatureLabel,
    feelsLikeLabel,
    humidityLabel,
    pressureLabel,
    windSpeedLabel,
    windDirectionLabel,
    visibilityLabel,
    cloudCoverLabel,
    precipitationLabel,
    uvIndexLabel,
  ].join(' | ');
}
