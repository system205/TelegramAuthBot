package com.system205.telegram.message;

import com.system205.entity.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.reactive.function.client.*;
import org.telegram.telegrambots.meta.api.objects.*;
import reactor.core.publisher.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetPasswordMessageProcessor implements MessageProcessor {
    private final WebClient webClient;
    @Override
    public Optional<String> process(Message message) {
        if (!message.hasText() || !message.getText().equals("/get_password")) return Optional.empty();

        TelegramUser user = TelegramUser.from(message.getFrom());

        try {
            Mono<Optional<String>> passwordResponse = webClient
                .post()
                .uri("api/telegram/auth")
                .body(Mono.just(user), TelegramUser.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToMono(String.class);
                    else if (response.statusCode().equals(HttpStatus.NOT_FOUND))
                        return null;
                    return null;
                }).map(Optional::ofNullable);

            Optional<String> password = Objects.requireNonNullElse(passwordResponse.block(), Optional.empty());
            return password
                .map(s -> "Here is you password: " + s)
                .or(() -> Optional.of("Sorry. We can't obtain your password. "));
        } catch (WebClientRequestException e) {
            log.error("User {} wants password. Can't connect to /api/telegram/auth. Details: {}",
                user.getUserName(), e.getMessage());
            return Optional.of("Sorry, the server is down. Try later");
        }

    }
}
