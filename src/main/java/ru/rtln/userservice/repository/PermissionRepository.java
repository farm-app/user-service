package ru.rtln.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.rtln.userservice.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query(value = """
            SELECT  u.id
             FROM   "user"."user" u
              JOIN  "user".role r
               ON   r.id = u.role_id
              JOIN  "user".permission_role pr
               ON   r.id = pr.role_id
              JOIN  "user".permission p
               ON   p.id = pr.permission_id AND
                    p.name = :permissionName
            """, nativeQuery = true)
    List<Long> findAllUsersWithPermission(String permissionName);

    @Modifying
    @Query(value = """
            DELETE FROM "user"."permission_role" pr
             WHERE  pr.permission_id = :permissionId
            """, nativeQuery = true)
    void deletePermissionRoleByPermissionId(Long permissionId);

    @Modifying
    @Query(value = """
            DELETE FROM "user"."permission_role" pr
             WHERE  pr.permission_id = :permissionId AND
                    pr.role_id != 2
            """, nativeQuery = true)
    void deletePermissionRoleByPermissionIdWithoutAdminRole(Long permissionId);

    @Modifying
    @Query(value = """
            DELETE FROM "user"."permission_role" pr
             WHERE  pr.permission_id = :permissionId AND
                    pr.role_id = :roleId
            """, nativeQuery = true)
    void deletePermissionRoleByPermissionIdAndRoleId(Long permissionId, Long roleId);

    @Modifying
    @Query(value = """
            DELETE FROM "user"."permission_role"
             WHERE  role_id IN (SELECT role_id FROM "user"."user" WHERE NOT active)
            """, nativeQuery = true)
    void deletePermissionsFromInactiveUsers();

    Optional<Permission> findByName(String name);
}