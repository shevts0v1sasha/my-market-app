package ru.yandex.marketapp.user.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.user.infrastructure.entity.UserEntity;
import ru.yandex.marketapp.user.infrastructure.repository.UserR2dbcRepository;

@Configuration
@RequiredArgsConstructor
public class UserDataInitializer {

    private final UserR2dbcRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner seedUsersRunner() {
        return args -> userRepository.count()
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.empty();
                    }
                    UserEntity user1 = new UserEntity(null, "user1",
                            passwordEncoder.encode("password"), true);
                    UserEntity user2 = new UserEntity(null, "user2",
                            passwordEncoder.encode("password"), true);
                    return userRepository.save(user1)
                            .then(userRepository.save(user2))
                            .then();
                })
                .block();
    }
}
