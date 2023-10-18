package com.system205;

import com.system205.entity.*;
import lombok.extern.slf4j.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;
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
            log.info("Bot is registered successfully");
        } catch (TelegramApiException e) {
            log.error("Can't register a bot", e);
        }
    }

    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, TelegramUser> kafkaTemplate) {
        return args -> {
            TelegramUser user = new TelegramUser(1L, "name", null, "username");
            kafkaTemplate.send("telegram", user).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("result {}", result);
                } else {
                    log.error("result {}", result, ex);
                }
            });
        };
    }
}
