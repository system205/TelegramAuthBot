package com.system205.telegram.message;


import com.system205.entity.*;
import com.system205.kafka.*;
import com.system205.service.*;
import com.system205.telegram.dto.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.*;

@Component
@RequiredArgsConstructor
public class StartMessageProcessor implements MessageProcessor{
    private final TelegramUserService service;
    private final KafkaService kafka;
    @Override
    public Optional<String> process(Message message) {
        if (!message.hasText() || !message.getText().equals("/start")) return Optional.empty();

        User user = message.getFrom();
        TelegramUser telegramUser = TelegramUser.from(user);

        boolean registered = service.registerUser(telegramUser);

        String registrationText;
        if (registered) {
            registrationText = "You're successfully registered!";
            kafka.send(new TelegramUserUpdate(telegramUser, telegramUser));
        }
        else registrationText = "You have already registered";


        String responseText = String.format("""
            Hi, %s! %s
            
            You can use:
            /get_password - to /*sign/* in on the website
            /start - to see this message""", telegramUser.getFirstName(), registrationText);
        return Optional.of(responseText);
    }
}
