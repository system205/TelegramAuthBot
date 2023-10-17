package com.system205.service;


import com.system205.entity.*;

public interface TelegramUserService {
    boolean registerUser(TelegramUser telegramUser);

    void blockUser(Long userId);

    void unblockUser(Long userId);
}
