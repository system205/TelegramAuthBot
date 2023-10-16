package com.telegram;

import com.system205.telegram.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.*;
import org.springframework.context.*;
import org.telegram.telegrambots.meta.generics.*;

@SpringBootTest(classes = {Bot.class})
class TelegramAuthBotApplicationTests {


    @Test
    void contextLoads(ApplicationContext context) {
        LongPollingBot bot = context.getBean(LongPollingBot.class);
        Assertions.assertEquals("TelegramAuthBot", bot.getBotUsername());
        Assertions.assertNotNull(bot.getBotToken());
    }

}
