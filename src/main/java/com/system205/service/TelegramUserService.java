package com.system205.service;


import com.system205.entity.TelegramUser;

import java.util.List;

public interface TelegramUserService {
    boolean registerUser(TelegramUser telegramUser);

    void blockUser(Long userId);

    void unblockUser(Long userId);

    List<TelegramUser> findAccessibleUsers();

    void updateUser(TelegramUser updatedUser);
}
