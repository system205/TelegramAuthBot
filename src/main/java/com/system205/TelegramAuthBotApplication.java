package com.system205;

import lombok.extern.slf4j.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.*;
import org.telegram.telegrambots.meta.*;
import org.telegram.telegrambots.meta.exceptions.*;
import org.telegram.telegrambots.meta.generics.*;
import org.telegram.telegrambots.updatesreceivers.*;

@SpringBootApplication
@Slf4j
public class TelegramAuthBotApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TelegramAuthBotApplication.class, args);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            LongPollingBot bot = context.getBean(LongPollingBot.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Can't register a bot", e);
        }
    }
}
