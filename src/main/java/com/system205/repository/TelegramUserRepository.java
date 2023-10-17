package com.system205.repository;

import com.system205.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
}
