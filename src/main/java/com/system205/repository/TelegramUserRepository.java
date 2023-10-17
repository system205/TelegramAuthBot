package com.system205.repository;

import com.system205.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    @Transactional
    @Modifying
    @Query("update TelegramUser t set t.blocked = false where t.id = ?1")
    void unblockUserById(Long id);
    @Transactional
    @Modifying
    @Query("update TelegramUser t set t.blocked = true where t.id = ?1")
    void blockUserById(Long id);
}
