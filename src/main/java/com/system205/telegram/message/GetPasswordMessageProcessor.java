package com.system205.telegram.message;

import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.*;

@Component
public class GetPasswordMessageProcessor implements MessageProcessor {
    @Override
    public Optional<String> process(Message message) {
        if (!message.hasText() || !message.getText().equals("/get_password")) return Optional.empty();

        String newText = "Here is your password: ABC";

        return Optional.of(newText);
    }
}
