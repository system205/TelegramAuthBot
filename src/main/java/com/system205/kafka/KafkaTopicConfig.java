package com.system205.kafka;

import com.system205.entity.*;
import lombok.extern.slf4j.*;
import org.apache.kafka.clients.admin.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.config.*;
import org.springframework.messaging.handler.annotation.*;

@Configuration
@ConditionalOnProperty(prefix = "telegram.kafka", name = "enabled", havingValue = "true")
@Slf4j
public class KafkaTopicConfig {
    @Bean
    public NewTopic telegramTopic(){
        return TopicBuilder.name("telegram").build();
    }

    @KafkaListener(topics = {"telegram"}, id = "telegram")
    void listener(@Payload TelegramUser data) {
        log.info("READ: {}", data);
    }
}
