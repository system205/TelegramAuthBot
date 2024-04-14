package com.system205.kafka;

import com.system205.telegram.dto.TelegramUserUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, TelegramUserUpdate> kafkaTemplate;
    @Value("${telegram.kafka.enabled:false}")
    private boolean kafkaEnabled;

    public void send(TelegramUserUpdate telegramUserUpdate) {
        if (kafkaEnabled)
            kafkaTemplate.send("telegramUserUpdate", telegramUserUpdate);
        else log.warn("Unable to send. Kafka is disabled. Enable with 'telegram.kafka.enabled'");
    }

}
