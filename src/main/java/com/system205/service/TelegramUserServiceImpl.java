package com.system205.service;

import com.system205.entity.*;
import com.system205.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public final class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository repository;

    @Override
    public boolean registerUser(TelegramUser telegramUser) {
        Optional<TelegramUser> optionalUser = repository.findById(telegramUser.getId());

        if (optionalUser.isPresent()) return false;

        TelegramUser user = repository.save(telegramUser);

        log.info("User[{}] {} was saved", user.getId(), user.getUserName());

        return true;
    }

    @Override
    public void blockUser(Long userId) {
        repository.blockUserById(userId);
        log.info("User[{}] was blocked", userId);
    }

    @Override
    public void unblockUser(Long userId) {
        repository.unblockUserById(userId);
        log.info("User[{}] was unblocked", userId);
    }
}
