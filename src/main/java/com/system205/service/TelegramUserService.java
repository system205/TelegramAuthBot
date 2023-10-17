package com.system205.service;


import com.system205.entity.*;

import java.util.*;

public interface TelegramUserService {
    boolean registerUser(TelegramUser telegramUser);

    void blockUser(Long userId);

    void unblockUser(Long userId);

    List<TelegramUser> findAccessibleUsers();

    void updateUser(TelegramUser updatedUser);
}
