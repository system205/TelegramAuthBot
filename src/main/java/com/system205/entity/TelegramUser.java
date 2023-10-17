package com.system205.entity;

import jakarta.persistence.*;
import lombok.*;
import org.telegram.telegrambots.meta.api.objects.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "telegram_user")
public final class TelegramUser {
    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String userName;
    @Column(columnDefinition = "boolean default false")
    private Boolean blocked = false;

    public TelegramUser() {
    }

    public TelegramUser(Long id, String firstName, String lastName, String userName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }


    public static TelegramUser from(User user) {
        return new TelegramUser(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName());
    }
}
