package ru.rtln.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@NamedEntityGraph(
        name = "graph.UserWithProfilePictures",
        attributeNodes = {
                @NamedAttributeNode(value = "profilePicture"),
                @NamedAttributeNode(value = "profilePictures")
        }
)
@Table(name = "\"user\"", schema = "\"user\"")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "patronymic", length = 50)
    private String patronymic;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "active")
    private boolean active;

    @Column(name = "date_work_from")
    private LocalDate dateWorkFrom;

    @Column(name = "date_work_to")
    private LocalDate dateWorkTo;

    @Column(name = "date_probation_end")
    private LocalDate dateProbationEnd;

    @JoinColumn(name = "position")
    private String positionName;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "address", length = 100)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_picture_id")
    private ProfilePicture profilePicture;

    @OneToMany(mappedBy = "user")
    private List<ProfilePicture> profilePictures;

    @Column(name = "gender_id")
    private Gender gender;

    public User(Long id) {
        this.id = id;
    }

    public Role setRole(Role role) {
        this.role = role;
        return role;
    }
}