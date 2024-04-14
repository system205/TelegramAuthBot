package com.system205.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

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

    public static TelegramUser from(Chat chat) {
        return new TelegramUser(chat.getId(), chat.getFirstName(), chat.getLastName(), chat.getUserName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TelegramUser that = (TelegramUser) o;

        if (!id.equals(that.id)) return false;
        if (!Objects.equals(firstName, that.firstName)) return false;
        if (!Objects.equals(lastName, that.lastName)) return false;
        return userName.equals(that.userName);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + userName.hashCode();
        return result;
    }
}
