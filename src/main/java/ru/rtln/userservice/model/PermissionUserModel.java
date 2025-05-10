package ru.rtln.userservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PermissionUserModel {

    @NotNull
    private String permissionName;

    @NotNull
    private Set<Long> userIds;

    public String getPermissionName() {
        return permissionName.toUpperCase();
    }
}