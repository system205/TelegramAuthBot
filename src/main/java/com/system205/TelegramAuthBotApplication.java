package com.system205;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@SpringBootApplication
public class TelegramAuthBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramAuthBotApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(ApplicationContext context) {
        return (String... args)-> {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(context.getBean(LongPollingBot.class));
                log.info("Bot is registered successfully");
            } catch (TelegramApiException e) {
                log.error("Can't register a bot", e);
            }
        };
    }

}

