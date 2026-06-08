package ru.yandex.marketapp.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.user.infrastructure.security.MarketUserDetails;

@Service
public class CurrentUserService {

    public Mono<Long> requireUserId() {
        return currentAuthentication()
                .map(auth -> {
                    if (auth.getPrincipal() instanceof MarketUserDetails details) {
                        return details.getUserId();
                    }
                    throw new AccessDeniedException("User is not authenticated");
                });
    }

    public Mono<Boolean> isAuthenticated() {
        return currentAuthentication()
                .map(Authentication::isAuthenticated)
                .defaultIfEmpty(false);
    }

    private Mono<Authentication> currentAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated);
    }
}
