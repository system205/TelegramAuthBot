package com.system205.telegram.message;


import com.system205.entity.*;
import com.system205.service.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.*;

@Component
@RequiredArgsConstructor
public class StartMessageProcessor implements MessageProcessor{
    private final TelegramUserService service;
    @Override
    public Optional<String> process(Message message) {
        if (!message.hasText() || !message.getText().equals("/start")) return Optional.empty();

        User user = message.getFrom();
        TelegramUser telegramUser = TelegramUser.from(user);

        boolean registered = service.registerUser(telegramUser);

        String welcomeText;
        if (registered) welcomeText = "You're successfully registered!";
        else welcomeText = "You have already registered";

        return Optional.of(welcomeText);
    }
}
