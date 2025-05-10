package ru.rtln.userservice.config.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import ru.rtln.userservice.entity.Permission;
import ru.rtln.userservice.entity.Role;
import ru.rtln.userservice.repository.PermissionRepository;
import ru.rtln.userservice.repository.RoleRepository;

import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Configuration
public class DictionaryTablesInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Bean
    @RequestScope
    public Map<Long, Role> roleIdToRole() {
        return roleRepository.findAllWithPermissions().stream()
                .collect(toMap(Role::getId, identity()));
    }

    @Bean
    @RequestScope
    public Map<String, Permission> permissionNameToPermission() {
        return permissionRepository.findAll().stream()
                .collect(toMap(Permission::getDisplayName, identity()));
    }
}
