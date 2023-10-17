package com.system205.telegram;


import com.system205.entity.*;
import com.system205.service.*;
import com.system205.telegram.exceptions.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.*;

@Component
@Slf4j
public final class Bot extends TelegramLongPollingBot {
    private final TelegramUserService service;

    @Autowired
    public Bot(@Value("${bot.token}") String botToken, TelegramUserService service) {
        super(botToken);
        this.service = service;
        log.info("Bot initialized");
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.trace("Receive update: {}", update);

        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText() && message.getText().equals("/start")) {
                processStartMessage(message);
            }

        }
    }

    private void processStartMessage(Message message) {
        User user = message.getFrom();
        TelegramUser telegramUser = TelegramUser.from(user);

        boolean registered = service.registerUser(telegramUser);

        String welcomeText;
        if (registered) welcomeText = "You're successfully registered!";
        else welcomeText = "You have already registered";

        Message sentMessage = sendMessage(user.getId(), welcomeText);
        log.debug("Message[{}]: '{}' sent to chat {}", sentMessage.getMessageId(), sentMessage.getText(), sentMessage.getChatId());
    }

    private Message sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            return execute(message);
        } catch (TelegramApiException exception) {
            log.error("Can't send a message '{}' to chat with id {}", text, chatId, exception);
            throw new SendMessageException("Can't send a message", exception);
        }

    }

    @Override
    public String getBotUsername() {
        return "TelegramAuthBot";
    }
}

