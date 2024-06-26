package com.system205.service;

import com.system205.entity.TelegramUser;
import com.system205.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<TelegramUser> findAccessibleUsers() {
        return repository.findByBlockedFalse();
    }

    @Override
    public void updateUser(TelegramUser updatedUser) {
        repository.save(updatedUser);
    }
}
