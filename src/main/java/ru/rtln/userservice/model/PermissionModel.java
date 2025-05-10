package ru.rtln.userservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PermissionModel {

    private Long id;

    @NotNull
    private String displayName;

    private String name;

    public PermissionModel(String displayName) {
        this.displayName = displayName;
    }
}