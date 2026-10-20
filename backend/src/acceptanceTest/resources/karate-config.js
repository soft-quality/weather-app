function fn() {
  // El runner inicia el backend en un puerto libre y entrega esta dirección.
  var baseUrl = karate.properties['weather.baseUrl'];
  if (!baseUrl) {
    throw new Error('Ejecuta la tarea Gradle acceptanceTest para iniciar el entorno de pruebas');
  }
  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 10000);
  return { baseUrl: baseUrl };
}
