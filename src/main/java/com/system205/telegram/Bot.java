package com.system205.telegram;


import com.system205.entity.TelegramUser;
import com.system205.kafka.KafkaService;
import com.system205.service.TelegramUserService;
import com.system205.telegram.dto.TelegramUserUpdate;
import com.system205.telegram.exceptions.SendMessageException;
import com.system205.telegram.message.MessageProcessor;
import com.system205.telegram.util.Utils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public final class Bot extends TelegramLongPollingBot {
    private static final String MARKDOWN = "MarkdownV2";
    private final TelegramUserService service;
    private final List<MessageProcessor> processors;
    private final KafkaService kafka;
    private Long botId;


    @Autowired
    public Bot(@Value("${bot.token}") String botToken, TelegramUserService service, List<MessageProcessor> processors, KafkaService kafka) {
        super(botToken);
        this.service = service;
        this.processors = processors;
        this.kafka = kafka;
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
                kafka.send(new TelegramUserUpdate(user, updatedUser));
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId),
            Utils.markdownEscapeCleaner(text));
        message.setParseMode(MARKDOWN);
        try {
            Message sentMessage = execute(message);
            log.debug("Message[{}]: '{}' sent to chat {}", sentMessage.getMessageId(), sentMessage.getText(), sentMessage.getChatId());
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

