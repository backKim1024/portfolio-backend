package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements HttpHandler {

    private final AuthHandler auth;

    public LoginHandler(AuthHandler auth) {
        this.auth = auth;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);
        String password = parsePassword(body);

        try {
            String sessionId = auth.login(password);
            exchange.getResponseHeaders()
                    .set("Content-Type", "text/plain; charset=UTF-8");
            exchange.getResponseHeaders()
                    .add("Set-Cookie", "SESSIONID=" + sessionId + "; HttpOnly; Secure; Path=/");
            byte[] resp = "로그인 성공".getBytes((StandardCharsets.UTF_8));
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (SecurityException se) {
            exchange.getResponseHeaders()
                    .set("Content-Type", "text/plain; charset=UTF-8");
            byte[] resp = se.getMessage().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(401, resp.length);
            exchange.getResponseBody().write(resp);
        } finally {
            exchange.close();
        }
    }

    private String parsePassword(String body) {
        for (String kv : body.split("&")) {
            String[] parts = kv.split("=");
            if (parts.length == 2 && "password".equals(parts[0])) {
                return parts[1];
            }
        }
        return "";
    }
}
