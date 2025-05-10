package ru.rtln.userservice.redis.repository;

import org.springframework.data.repository.CrudRepository;
import ru.rtln.userservice.redis.entity.RedisUser;

public interface RedisUserRepository extends CrudRepository<RedisUser, Long> {
}