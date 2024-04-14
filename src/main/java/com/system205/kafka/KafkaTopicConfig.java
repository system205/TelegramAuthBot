package com.system205.kafka;

import com.system205.telegram.dto.TelegramUserUpdate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "telegram.kafka", name = "enabled", havingValue = "true")
public class KafkaTopicConfig {
    @Bean
    public NewTopic telegramTopic() {
        return TopicBuilder.name("telegramUserUpdate").build();
    }

    @KafkaListener(topics = {"telegramUserUpdate"}, id = "telegramLogger")
    void listener(TelegramUserUpdate data) {
        log.info("New telegram user update in kafka: {}", data);
    }
}
