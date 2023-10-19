package com.system205.kafka;

import com.system205.telegram.dto.*;
import lombok.extern.slf4j.*;
import org.apache.kafka.clients.admin.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.config.*;

@Configuration
@ConditionalOnProperty(prefix = "telegram.kafka", name = "enabled", havingValue = "true")
@Slf4j
public class KafkaTopicConfig {
    @Bean
    public NewTopic telegramTopic(){
        return TopicBuilder.name("telegramUserUpdate").build();
    }

    @KafkaListener(topics = {"telegramUserUpdate"}, id = "telegramLogger")
    void listener(TelegramUserUpdate data) {
        log.info("New telegram user update in kafka: {}", data);
    }
}
