package ru.yandex.marketapp.config;

import java.util.List;

public record ValidationExceptionDto(String message,
                                     List<String> errors) {
}
