package com.system205.kafka;

import com.system205.telegram.dto.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    @Value("${telegram.kafka.enabled:false}")
    private boolean kafkaEnabled;
    private final KafkaTemplate<String, TelegramUserUpdate> kafkaTemplate;

    public void send(TelegramUserUpdate telegramUserUpdate) {
        if (kafkaEnabled)
            kafkaTemplate.send("telegramUserUpdate", telegramUserUpdate);
        else log.warn("Unable to send. Kafka is disabled. Enable with 'telegram.kafka.enabled'");
    }

}
