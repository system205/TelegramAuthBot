package com.system205.telegram.message;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

public interface MessageProcessor {
    Optional<String> process(Message message);
}
