package com.system205.telegram;


import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.objects.*;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {
    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
        log.info("Bot initialized");
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.trace("Receive update: {}", update);
    }

    @Override
    public String getBotUsername() {
        return "TelegramAuthBot";
    }
}

