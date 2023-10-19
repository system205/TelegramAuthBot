package com.system205.telegram;


import com.system205.entity.*;
import com.system205.service.*;
import com.system205.telegram.dto.*;
import com.system205.telegram.exceptions.*;
import com.system205.telegram.message.*;
import jakarta.annotation.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.kafka.core.*;
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
    private final List<MessageProcessor> processors;
    private final KafkaTemplate<String, TelegramUserUpdate> kafkaTemplate;
    private Long botId;
    @Value("${telegram.kafka.enabled:false}")
    private boolean kafkaEnabled;


    @Autowired
    public Bot(@Value("${bot.token}") String botToken, TelegramUserService service, List<MessageProcessor> processors, KafkaTemplate<String, TelegramUserUpdate> kafkaTemplate) {
        super(botToken);
        this.service = service;
        this.processors = processors;
        this.kafkaTemplate = kafkaTemplate;
        log.info("Bot initialized. {} message processors were injected", processors.size());
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
            Long userId = message.getFrom().getId();

            for (var m : processors) {
                m.process(message).ifPresent(result -> sendMessage(userId, result));
            }

        } else if (update.hasMyChatMember()) {
            ChatMemberUpdated myChatMember = update.getMyChatMember();
            Long userId = myChatMember.getFrom().getId();
            handleNewUserStatus(myChatMember, userId);
        }
    }

    private void sendToKafka(TelegramUserUpdate telegramUserUpdate) {
        if (kafkaEnabled)
            kafkaTemplate.send("telegramUserUpdate", telegramUserUpdate);
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

    @Scheduled(fixedRateString = "${bot.update.rate}", initialDelayString = "${bot.update.initial-delay}")
    private void updateTelegramUsers() {
        List<TelegramUser> users = service.findAccessibleUsers();
        log.debug("Start checking {} users on updates", users.size());

        for (TelegramUser user : users) {
            TelegramUser updatedUser = getTelegramUserById(user.getId());
            if (!user.equals(updatedUser)) { // User has changed
                service.updateUser(updatedUser); // Save new updated info
                log.info("User[{}] was updated. Before: {}. After: {}", user.getId(), user, updatedUser);

                // Send update message to kafka
                sendToKafka(new TelegramUserUpdate(user, updatedUser));
            }
        }
    }

    private Message sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            Message sentMessage = execute(message);
            log.debug("Message[{}]: '{}' sent to chat {}", sentMessage.getMessageId(), sentMessage.getText(), sentMessage.getChatId());
            return sentMessage;
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

