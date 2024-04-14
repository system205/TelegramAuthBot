package com.system205.telegram.dto;

import com.system205.entity.TelegramUser;

public record TelegramUserUpdate(TelegramUser oldUser, TelegramUser updatedUser) {
}
