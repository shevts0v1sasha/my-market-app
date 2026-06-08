package ru.yandex.marketapp.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("authenticated")
    public Mono<Boolean> authenticated() {
        return currentAuthentication()
                .map(auth -> auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
                .defaultIfEmpty(false);
    }

    @ModelAttribute("username")
    public Mono<String> username() {
        return currentAuthentication()
                .map(Authentication::getName)
                .defaultIfEmpty("");
    }

    private Mono<Authentication> currentAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(auth -> auth != null);
    }
}
