package ru.yandex.marketapp.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.common.application.BusinessRuleException;
import ru.yandex.marketapp.user.infrastructure.entity.UserEntity;
import ru.yandex.marketapp.user.infrastructure.repository.UserR2dbcRepository;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserR2dbcRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<Void> register(String username, String password) {
        if (username == null || username.isBlank()) {
            return Mono.error(new BusinessRuleException("Username is required"));
        }
        if (password == null || password.length() < 4) {
            return Mono.error(new BusinessRuleException("Password must be at least 4 characters"));
        }

        return userRepository.existsByUsername(username)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessRuleException("Username already exists"));
                    }
                    UserEntity user = new UserEntity(null, username, passwordEncoder.encode(password), true);
                    return userRepository.save(user).then();
                });
    }
}
