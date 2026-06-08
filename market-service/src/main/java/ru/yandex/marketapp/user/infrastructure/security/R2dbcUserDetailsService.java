package ru.yandex.marketapp.user.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.user.infrastructure.repository.UserR2dbcRepository;

@Service
@RequiredArgsConstructor
public class R2dbcUserDetailsService implements ReactiveUserDetailsService {

    private final UserR2dbcRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .<UserDetails>map(user -> new MarketUserDetails(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.isEnabled()))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)));
    }
}
