package ru.rtln.userservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.rtln.userservice.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"profilePicture"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithManyToOneFields(Long id);

    @EntityGraph(value = "graph.UserWithProfilePictures")
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithProfilePictures(Long id);

    @Query("""
            SELECT  u
             FROM   User u
               LEFT JOIN FETCH u.role r
               LEFT JOIN FETCH r.permissions
             WHERE  u.id = :id
            """)
    Optional<User> findByIdWithRole(Long id);

    @Query("""
            SELECT  u
             FROM   User u
               LEFT JOIN FETCH u.role r
               LEFT JOIN FETCH r.permissions
             WHERE  u.id = :id
            """)
    Optional<User> findRealByIdWithRole(Long id);

    @Query("""
            SELECT  u
             FROM   User u
               LEFT JOIN FETCH u.role r
               LEFT JOIN FETCH r.permissions
             WHERE  u.email = :email
            """)
    Optional<User> findByEmailWithRole(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"profilePicture"})
    @Query("SELECT u FROM User u")
    Set<User> findAllWithManyToOneFields();

    @Query("""
            SELECT  u
             FROM User u
               LEFT JOIN FETCH u.role r
               LEFT JOIN FETCH r.permissions
            """)
    List<User> findAllWithRole();
}