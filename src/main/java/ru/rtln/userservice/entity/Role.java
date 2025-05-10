package ru.rtln.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Table(name = "role", schema = "\"user\"")
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @OneToMany(mappedBy = "role", orphanRemoval = true)
    private List<User> users;

    @ManyToMany
    @JoinTable(
            schema = "\"user\"",
            name = "permission_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;

    public Role(boolean isAdmin) {
        this.id = isAdmin ? 2L : 1L;
    }

    public Role(Long id) {
        this.id = id;
    }

    public Role() {
        this.name = UUID.randomUUID().toString();
    }

    public Role setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public void addPermission(Permission permission) {
        if (permissions == null) {
            permissions = new HashSet<>();
        }
        permissions.add(permission);
    }

    public Role clearPermissions() {
        if (permissions != null) {
            permissions.clear();
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}