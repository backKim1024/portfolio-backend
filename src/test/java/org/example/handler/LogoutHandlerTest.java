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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutHandlerTest {

    @Mock
    private AuthHandler authHandler;

    @InjectMocks
    private  LogoutHandler logoutHandler;

    private HttpExchange exchange;
    private Headers responseHeaders;
    private ByteArrayOutputStream responseBody;

    @BeforeEach
    void setUp() throws Exception {
        responseHeaders = new Headers();
        responseBody = new ByteArrayOutputStream();

        exchange = mock(HttpExchange.class);
        lenient().when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        lenient().when(exchange.getResponseBody()).thenReturn(responseBody);
    }

    @Test
    void handle_nonPostMethod_return405() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");

        logoutHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
        verify(exchange).close();
    }

    @Test
    void handle_validSession_logoutSuccess() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        // 쿠기 헤더에 세션ID 제공
        Headers requestHeaders = new Headers();
        requestHeaders.add("Cookie", "SESSIONID=ABC");
        when(exchange.getRequestHeaders()).thenReturn(requestHeaders);
        when(authHandler.isValidSession("ABC")).thenReturn(true);

        logoutHandler.handle(exchange);

        // 로그아웃 성공 메시지 및 상태 코드 확인
        String resp = responseBody.toString(StandardCharsets.UTF_8);
        assertEquals("로그아웃 성공", resp);
        verify(exchange).sendResponseHeaders(200, resp.getBytes().length);

        // AuthHandler.logout. 호출 검증
        verify(authHandler).logout("ABC");
        verify(exchange).close();
    }

    @Test
    void handle_invalidOrNoSession_returns400() throws  IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        // 쿠기가 없거나, isValiSesston=false인 경우
        when(exchange.getRequestHeaders()).thenReturn(new Headers());
        lenient().when(authHandler.isValidSession(anyString())).thenReturn(false);

        logoutHandler.handle(exchange);

        String resp = responseBody.toString(StandardCharsets.UTF_8);
        assertEquals("유효한 세션이 없습니다.", resp);
        verify(exchange).sendResponseHeaders(400, resp.getBytes().length);
        verify(exchange).close();
    }

}