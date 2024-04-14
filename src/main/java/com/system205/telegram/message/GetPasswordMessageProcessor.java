package com.system205.telegram.message;

import com.system205.entity.TelegramUser;
import com.system205.telegram.dto.TelegramAuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class GetPasswordMessageProcessor implements MessageProcessor {
    private final WebClient webClient;
    private final String uri;

    public GetPasswordMessageProcessor(WebClient webClient,
                                       @Value("${web-client.uri.get-password:api/telegram/auth}") String uri) {
        this.webClient = webClient;
        this.uri = uri;
    }

    @Override
    public Optional<String> process(Message message) {
        if (!message.hasText() || !message.getText().equals("/get_password")) return Optional.empty();

        TelegramUser user = TelegramUser.from(message.getFrom());

        try {
            Mono<TelegramAuthResponse> passwordResponse = webClient
                .post()
                .uri(uri)
                .body(Mono.just(user), TelegramUser.class)
                .exchangeToMono(response -> {
                    log.info("Get password response status: {}", response.statusCode());
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToMono(TelegramAuthResponse.class);
                    else if (response.statusCode().equals(HttpStatus.NOT_FOUND))
                        return Mono.empty();
                    return Mono.empty();
                });

            Optional<TelegramAuthResponse> password = Optional.ofNullable(passwordResponse.block());
            return password
                .map(s -> "Here is your password: " + s.password())
                .or(() -> Optional.of("Sorry. We can't obtain your password. "));
        } catch (WebClientRequestException e) {
            log.error("User {} wants password. Can't connect to /{}. Details: {}",
                user.getUserName(), uri, e.getMessage());
            return Optional.of("Sorry, the server is down. Try later");
        }

    }
}
