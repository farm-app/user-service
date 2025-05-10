package ru.rtln.userservice.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import ru.rtln.userservice.entity.Gender;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("redis-user")
public class RedisUser {

    @Id
    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String patronymic;

    @Indexed
    private Boolean active;

    private Gender gender;

    private LocalDate birthday;

    private List<String> permissions;
}
