package ru.yandex.marketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManager reactiveAuthenticationManager) {
        RedirectServerAuthenticationSuccessHandler successHandler =
                new RedirectServerAuthenticationSuccessHandler();
        successHandler.setLocation(URI.create("/items"));

        RedirectServerLogoutSuccessHandler logoutSuccessHandler =
                new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/items?logout"));

        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/css/**", "/*.jpg", "/*.png", "/*.jpeg", "/*.gif", "/*.webp").permitAll()
                        .pathMatchers("/login", "/register", "/access-denied").permitAll()
                        .pathMatchers(HttpMethod.POST, "/register").permitAll()
                        .pathMatchers(HttpMethod.GET, "/", "/items", "/items/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/items/**", "/cart/**", "/buy").authenticated()
                        .pathMatchers("/cart/**", "/orders/**").authenticated()
                        .anyExchange().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, exception) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
                            return exchange.getResponse().setComplete();
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getResponse().getHeaders().setLocation(URI.create("/access-denied"));
                            return exchange.getResponse().setComplete();
                        }))
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(successHandler))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler))
                .authenticationManager(reactiveAuthenticationManager)
                .build();
    }
}
