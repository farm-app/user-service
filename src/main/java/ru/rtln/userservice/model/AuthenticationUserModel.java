package ru.rtln.userservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AuthenticationUserModel {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String patronymic;

    private Boolean enabled;

    private Boolean admin;

    private List<String> permissions;

    private String city;
}