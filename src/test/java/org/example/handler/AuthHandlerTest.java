package org.example.handler;

import org.example.model.User;
import org.example.repository.passwordRepository.MemoryPasswrodRepository;
import org.example.repository.passwordRepository.PasswordRepository;
import org.example.sesstion.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthHandlerTest {
    private AuthHandler authHandler;
    private SessionManager sessionManager;
    private PasswordRepository passwordRepository;

    @BeforeEach
    void setUp() {
        // 세션 타임아웃을 넉넉히 줘서 자동 만료는 안 일어나도록 설정
       long sessionTimeoutMillis = 30 * 60 * 1000; // 30분
       long cleanupIntervalMillis = 5 * 60 * 1000; // 5분
       sessionManager = new SessionManager(sessionTimeoutMillis, cleanupIntervalMillis);
       passwordRepository = new MemoryPasswrodRepository();
       authHandler = new AuthHandler(passwordRepository, sessionManager);
    }

    @AfterEach
    void tearDown() {
        sessionManager.shutdown();
    }

    @Test
    void guestLoginSuccess() {
        // 게스트인 경우
        String guestPwd = passwordRepository.getGuestPassword();
        String sessionId = authHandler.login(guestPwd);

        assertNotNull(sessionId);
        assertTrue(authHandler.isValidSession(sessionId));

        User user = authHandler.getUserFromSession(sessionId);
        assertFalse(user.isAdmin(), "User.isAdmin()이 false여야 한다.");
    }

    @Test
    void loginFailureThrowsException() {
        // 틀린 비밀번호로 로그인 시도
        assertThrows(SecurityException.class,() -> authHandler.login("wrong-password"));
    }

    @Test
    void logoutInvalidatesSession() {
        // 로그인 후 로그 아우스 -> 세션 무효화
        String sessionId = authHandler.login(passwordRepository.getAdminPassword());
        assertTrue(authHandler.isValidSession(sessionId));

        authHandler.logout(sessionId);
        assertFalse(authHandler.isValidSession(sessionId), "로그아웃 후 세션은 유효하지 않아야 한다.");
    }

    @Test
    void sessionExpiration() throws InterruptedException {
        // 짧은 타임아웃으로 재설정하여 자동 만료 테스트
        sessionManager.shutdown();
        sessionManager = new SessionManager(50,50); // 50ms 타임아웃
        authHandler = new AuthHandler(passwordRepository,sessionManager);

        String sessionId = authHandler.login(passwordRepository.getAdminPassword());
        assertTrue(authHandler.isValidSession(sessionId));

        Thread.sleep(100);  // 타임아웃 (50ms) 이후 대기
        assertFalse(authHandler.isValidSession(sessionId), "만료 기간이 지나면 세션은 무효화되어야 한다.");
    }
}