package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LogoutHandler implements HttpHandler {
    private final AuthHandler auth;

    public LogoutHandler(AuthHandler auth) {
        this.auth = auth;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(!"POST".equalsIgnoreCase((exchange.getRequestMethod()))) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        String sessionId = parseSessionId(cookie);

        if (sessionId != null && auth.isValidSession(sessionId)) {
            auth.logout(sessionId);
            byte[] resp = "로그아웃 성공".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } else {
            byte[] resp = "유효한 세션이 없습니다.".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, resp.length);
            exchange.getResponseBody().write(resp);
        }
        exchange.close();
    }

    private String parseSessionId(String cookie) {
        if (cookie == null) return null;
        for (String part : cookie.split(";")) {
            String[] kv = part.trim().split("=");
            if (kv.length == 2 && "SESSIONID".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }
}
