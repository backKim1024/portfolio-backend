package org.example.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

    @Mock
    private AuthHandler authHandler;

    @InjectMocks
    private LoginHandler loginHandler;

    private HttpExchange exchange;
    private Headers responseHeaders;
    private ByteArrayOutputStream responseBody;

    @BeforeEach
    void setUp() throws Exception {
        // 실제 응답 헤더/바디 객체 준비
        responseHeaders = new Headers();
        responseBody = new ByteArrayOutputStream();

        // HttpExchange mock
        exchange = mock(HttpExchange.class);
        lenient().when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        lenient().when(exchange.getResponseBody()).thenReturn(responseBody);
    }

    @Test
    void handle_nonPostMethod_returns405() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");

        loginHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
        verify(exchange).close();
    }

    @Test
    void handle_validLogin_setsCookieAnd200() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        // 비밀번호 파라미터 시뮬레이션
        String body = "password=correct";
        when(exchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        // AuthHandler.login() 동작 정의
        when(authHandler.login("correct")).thenReturn("SESSION123");

        loginHandler.handle(exchange);

        // 쿠기 설정 확인
        assertEquals("SESSIONID=SESSION123; HttpOnly; Secure; Path=/", responseHeaders.getFirst("Set-Cookie"));

        // 응답 바디 확인
        String resp = responseBody.toString(StandardCharsets.UTF_8);
        assertEquals("로그인 성공", resp);

        // 상태 코드 확인
        verify(exchange).sendResponseHeaders(200, resp.getBytes().length);
        verify(exchange).close();
    }

    @Test
    void handler_invalidLogin_returns401WithMessage() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        String body = "password=wrong";
        when(exchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(body.getBytes((StandardCharsets.UTF_8))));
        when(authHandler.login("wrong"))
                .thenThrow(new SecurityException("암호가 일치하지 않습니다."));

        loginHandler.handle(exchange);

        // 401 상태와 에러 메시지 확인
        String resp = responseBody.toString(StandardCharsets.UTF_8);
        assertEquals("암호가 일치하지 않습니다.", resp);
        verify(exchange).sendResponseHeaders(401, resp.getBytes().length);
        verify(exchange).close();
    }
}
