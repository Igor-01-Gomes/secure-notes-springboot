package com.igor.securenotes.service;

import com.igor.securenotes.model.Role;
import com.igor.securenotes.model.UserEntity;
import com.igor.securenotes.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity registerUser(String username, String rawPassword) {
        validateCredentials(username, rawPassword);

        if (userRepository.existsByUsernameIgnoreCase(username.trim())) {
            throw new IllegalArgumentException("Användarnamnet finns redan.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username.trim());
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public Optional<UserEntity> login(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return Optional.empty();
        }

        return userRepository.findByUsernameIgnoreCase(username.trim())
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    public void changePassword(UserEntity user, String currentPassword, String newPassword) {
        validateCredentials(user.getUsername(), newPassword);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Nuvarande lösenord är fel.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void createAdminIfMissing(String username, String rawPassword) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(rawPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Användarnamn måste vara minst 3 tecken.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Lösenord måste vara minst 6 tecken.");
        }
    }
}
