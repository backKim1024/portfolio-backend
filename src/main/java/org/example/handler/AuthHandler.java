package org.example.handler;

import org.example.model.User;
import org.example.repository.passwordRepository.PasswordRepository;

public class AuthHandler {

    private final PasswordRepository passwordRepository;

    public AuthHandler(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    // 로그인 처리 메서드
    public User login(String inputPassword) {
        if (passwordRepository.getAdminPassword().equals(inputPassword)) {
            return new User(true);
        } else if (passwordRepository.getGuestPassword().equals(inputPassword)) {
            return new User(false);
        } else {
            throw new SecurityException("암호가 일치하지 않습니다.");
        }
    }
}
