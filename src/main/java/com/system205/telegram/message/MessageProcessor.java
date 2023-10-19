package com.system205.telegram.message;

import org.telegram.telegrambots.meta.api.objects.*;

import java.util.*;

public interface MessageProcessor {
    Optional<String> process(Message message);
}
