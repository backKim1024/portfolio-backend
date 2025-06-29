package org.example.handler;

import org.example.model.User;
import org.example.repository.passwordRepository.PasswordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthHandlerTest {
    private AuthHandler authHandler;

    // 테스트용 메모리 저장소 구현
    private static class TestPasswordRepository implements PasswordRepository {

        @Override
        public String getAdminPassword() {
            return "qwer1234";
        }

        @Override
        public String getGuestPassword() {
            return "123456";
        }
    }

    @BeforeEach
    public void setUp() {
        authHandler = new AuthHandler(new TestPasswordRepository());
    }

    @Test
    public void testLoginWithAdminPassword() {
        User user = authHandler.login("qwer1234");
        assertTrue(user.isAdmin());
    }

    @Test
    public void testLoginWithGuessPassword() {
        User user = authHandler.login("123456");
        assertFalse(user.isAdmin());
    }

    @Test
    public void testLoginWithWrongPassword() {
        assertThrows(SecurityException.class, () -> {
            authHandler.login("wroongpass");
        });
    }
}