package ru.yandex.marketapp.user.infrastructure.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.user.application.UserRegistrationService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationService registrationService;

    @GetMapping("/login")
    public Mono<String> login(ServerWebExchange exchange, Model model) {
        var params = exchange.getRequest().getQueryParams();
        model.addAttribute("showRegistered", params.containsKey("registered"));
        model.addAttribute("showError", params.containsKey("error"));
        model.addAttribute("showLogout", params.containsKey("logout"));
        return Mono.just("login");
    }

    @GetMapping("/register")
    public Mono<String> registerForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return Mono.just("register");
    }

    @PostMapping("/register")
    public Mono<String> register(@Validated @ModelAttribute("form") RegistrationForm form, Model model) {
        return registrationService.register(form.getUsername(), form.getPassword())
                .then(Mono.just("redirect:/login?registered"))
                .onErrorResume(error -> {
                    model.addAttribute("form", form);
                    model.addAttribute("error", error.getMessage());
                    return Mono.just("register");
                });
    }

    @GetMapping("/access-denied")
    public Mono<String> accessDenied() {
        return Mono.just("access-denied");
    }

    @Data
    public static class RegistrationForm {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }
}
