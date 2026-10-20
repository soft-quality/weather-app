package com.weatherapp.backend.acceptance;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Proveedor HTTP local con datos fijos. El backend conserva sus servicios reales. */
final class WeatherProviderFixture implements AutoCloseable {
    private final HttpServer server;

    WeatherProviderFixture() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/search", this::geocoding);
        server.createContext("/forecast", this::forecast);
        server.start();
    }

    String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    private void geocoding(HttpExchange exchange) throws IOException {
        String name = parameters(exchange).getOrDefault("name", "");
        String body = switch (name) {
            case "Bogotá", "Bog" -> resource("city.json");
            default -> "{\"results\":[]}";
        };
        respond(exchange, body);
    }

    private void forecast(HttpExchange exchange) throws IOException {
        Map<String, String> query = parameters(exchange);
        // Latitud -1 representa una respuesta incompleta del proveedor para PA-API-07.
        String body = "-1.0".equals(query.get("latitude"))
                ? "{\"current\":null,\"daily\":null}" : resource("forecast.json");
        respond(exchange, body);
    }

    private static Map<String, String> parameters(HttpExchange exchange) {
        String raw = exchange.getRequestURI().getRawQuery();
        if (raw == null || raw.isEmpty()) {
            return Map.of();
        }
        return Arrays.stream(raw.split("&"))
                .map(part -> part.split("=", 2))
                .collect(Collectors.toMap(
                        pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        pair -> pair.length == 2
                                ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : ""));
    }

    private static String resource(String name) throws IOException {
        try (var input = Objects.requireNonNull(WeatherProviderFixture.class
                .getResourceAsStream("/acceptance/fixtures/" + name))) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void respond(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
