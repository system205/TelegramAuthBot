package com.system205.telegram.dto;

import com.system205.entity.*;

public record TelegramUserUpdate(TelegramUser oldUser, TelegramUser updatedUser) {
}
