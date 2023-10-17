package com.system205.service;

import com.system205.entity.*;
import com.system205.repository.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public final class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository repository;

    @Override
    public boolean registerUser(TelegramUser telegramUser) {
        Optional<TelegramUser> optionalUser = repository.findById(telegramUser.getId());

        if (optionalUser.isPresent()) return false;

        repository.save(telegramUser);

        return true;
    }
}
