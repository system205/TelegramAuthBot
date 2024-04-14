package com.system205.repository;

import com.system205.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    List<TelegramUser> findByBlockedFalse();

    @Transactional
    @Modifying
    @Query("update TelegramUser t set t.blocked = false where t.id = ?1")
    void unblockUserById(Long id);

    @Transactional
    @Modifying
    @Query("update TelegramUser t set t.blocked = true where t.id = ?1")
    void blockUserById(Long id);
}
