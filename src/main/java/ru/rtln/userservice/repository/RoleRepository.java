package ru.rtln.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.rtln.userservice.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = """
            SELECT r
             FROM Role r
             LEFT JOIN FETCH r.permissions
            """)
    List<Role> findAllWithPermissions();

    @Query(value = """
            SELECT r
             FROM Role r
             LEFT JOIN FETCH r.permissions
             WHERE r.id = :id
            """)
    Optional<Role> findByIdWithPermissions(Long id);

    @Modifying
    @Query(value = """
            UPDATE  User u
             SET    u.role = :role
             WHERE  u.id = :userId
            """)
    void setRoleToUser(Role role, Long userId);
}