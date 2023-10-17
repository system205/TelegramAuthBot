package com.system205.telegram;


import com.system205.entity.*;
import com.system205.service.*;
import com.system205.telegram.exceptions.*;
import jakarta.annotation.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.*;
import org.telegram.telegrambots.meta.exceptions.*;

import java.util.*;

@Component
@Slf4j
@EnableScheduling
public final class Bot extends TelegramLongPollingBot {
    private final TelegramUserService service;
    private Long botId;

    @Autowired
    public Bot(@Value("${bot.token}") String botToken, TelegramUserService service) {
        super(botToken);
        this.service = service;
        log.info("Bot initialized");
    }

    @PostConstruct
    private void init() {
        try {
            this.botId = getMe().getId();
        } catch (TelegramApiException e) {
            log.error("Can't call getMe()", e);
        }

        log.info("Bot id - {}", this.botId);
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.trace("Receive update: {}", update);

        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText() && message.getText().equals("/start")) {
                processStartMessage(message);
            }

        } else if (update.hasMyChatMember()) {
            ChatMemberUpdated myChatMember = update.getMyChatMember();
            Long userId = myChatMember.getFrom().getId();
            handleNewUserStatus(myChatMember, userId);
        }
    }

    private void handleNewUserStatus(ChatMemberUpdated myChatMember, Long userId) {
        ChatMember newChatMember = myChatMember.getNewChatMember();
        if (newChatMember.getUser().getId().equals(this.botId)) {
            if (newChatMember.getStatus().equals("kicked"))
                service.blockUser(userId);
            else if (newChatMember.getStatus().equals("member"))
                service.unblockUser(userId);
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

    @Scheduled(fixedRateString = "${bot.update.rate}", initialDelayString = "${bot.update.initial-delay}")
    private void updateTelegramUsers() {
        List<TelegramUser> users = service.findAccessibleUsers();
        log.debug("Start checking {} users on updates", users.size());
        for (TelegramUser user : users) {
            TelegramUser updatedUser = getTelegramUserById(user.getId());
            if (!user.equals(updatedUser)) {
                service.updateUser(updatedUser);
                log.info("User[{}] was updated. Before: {}. After: {}", user.getId(), user, updatedUser);
            }
        }
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

    private TelegramUser getTelegramUserById(long chatId) {
        try {
            Chat chat = execute(new GetChat(String.valueOf(chatId)));
            return TelegramUser.from(chat);
        } catch (TelegramApiException e) {
            log.warn("Can't retrieve telegram user from chat {}", chatId);
            throw new IllegalArgumentException("Can't get telegram user by chatId " + chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "TelegramAuthBot";
    }
}

