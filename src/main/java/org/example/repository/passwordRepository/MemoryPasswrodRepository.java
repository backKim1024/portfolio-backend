package org.example.repository.passwordRepository;

public class MemoryPasswrodRepository implements PasswordRepository {
    @Override
    public String getAdminPassword() {
        return "qwer1234";
    }

    @Override
    public String getGuestPassword() {
        return "123456";
    }
}
