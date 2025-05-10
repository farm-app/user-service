package ru.rtln.userservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@SuperBuilder
public class UserModel {

    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String lastName;

    @NotNull
    private String positionName;

    @NotNull
    private String firstName;

    private String patronymic;

    @NotNull
    private LocalDate birthday;

    private boolean active;

    private String gender;

    @NotNull
    private LocalDate dateWorkFrom;

    private LocalDate dateWorkTo;

    private LocalDate dateProbationEnd;

    @NotNull
    private String city;

    @NotNull
    private String address;

    private RoleModel role;

    private String profilePictureUrl;
}