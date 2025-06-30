package org.example.handler;

import com.sun.net.httpserver.*;
import org.example.model.User;
import org.example.repository.passwordRepository.PasswordRepository;
import org.example.sesstion.SessionManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class AuthHandler {
    private final PasswordRepository passwordRepository;
    private final SessionManager sessionManager;

    public AuthHandler(PasswordRepository passwordRepository, SessionManager sessionManager) {
        this.passwordRepository = passwordRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * 로그인 처리 메서드
     * @param inputPassword 클라이언트가 입력한 비밀번호
     * @return 생성된 sessionId
     * @throws SecurityException 인증 실패 시 예외 발생
     */
    public String login (String inputPassword) {
        User user;
        if (passwordRepository.getAdminPassword().equals(inputPassword)) {
            user = new User(true);
        } else if (passwordRepository.getGuestPassword().equals(inputPassword)) {
            user = new User(false);
        } else {
            throw new SecurityException("암호가 일치하지 않습니다.");
        }
        // 로그인 성공 시 세션 생성 및 sessionId 반환
        return sessionManager.createSession(user);
    }

    /**
     * 세션 유효성 검사
     * @param sessionId 클라이언트가 전달한 세션 토근
     * @return 세션이 유효하면 true, 아니면 false
     */
    public boolean isValidSession(String sessionId) {
        return sessionManager.isValidSession(sessionId);
    }

    /**
     * 세션으로부터 User 정보 조회
     * @param sessionId 클라이언트 세션 토근
     * @return User 객체 (세션 없거나 만료 시 null)
     */
    public User getUserFromSession(String sessionId) {
        return sessionManager.getUser(sessionId);
    }

    /**
     * 로그아웃 처리 (세션 삭제)
     * @param sessionId 제거할 세션 토큰
     */
    public void logout (String sessionId) {
        sessionManager.removeSession(sessionId);
    }
}
