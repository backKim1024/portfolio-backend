package org.example.filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.example.handler.AuthHandler;

import java.io.IOException;

public class SessionFilter extends Filter {

    private final AuthHandler authHandler;

    public SessionFilter(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        // 1) 요청 헤더에서 SESSIONID 쿠기 추출
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        String sessionId = null;
        if (cookie != null) {
            for (String part : cookie.split(";")) {
                String[] kv = part.trim().split("=");
                if (kv.length == 2 && "SESSIONID".equals(kv[0])) {
                    sessionId = kv[1];
                    break;
                }
            }
        }
        // 2) 새션 검증
        if (sessionId != null && authHandler.isValidSession(sessionId)) {
            // 유효하면 다음 필터/핸들러로 진행
            chain.doFilter(exchange);
        } else {
            // 무효하면 401 Unauthorized 응답
            String resp = "인증이 필요합니다.";
            exchange.sendResponseHeaders(401, resp.getBytes().length);
            exchange.getResponseBody().write(resp.getBytes());
            exchange.close();
        }
    }

    @Override
    public String description() {
        return "세션 유효성 검증 필터";
    }
}
