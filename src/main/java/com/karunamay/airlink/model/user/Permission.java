package com.karunamay.airlink.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_permission_name", columnNames = "name")
        },
        indexes = {
                @Index(name = "name", columnList = "name")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "roles")
@Builder
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Permission name is required")
    @Size(min = 3, max = 100, message = "Permission name must be between 3 and 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "resource", length = 50)
    private String resource;

    @Column(name = "action", length = 50)
    private String action;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Transient
    public String getFullPermission() {
        if (resource != null && action != null) {
            return resource + ":" + action;
        }
        return name;
    }

    public boolean isAssigned() {
        return roles != null && !roles.isEmpty();
    }

    @PrePersist
    protected void onCreate() {
        if (active == null) active = true;
        if (name != null) {
            if (name.contains(":")) {
                String[] parts = name.split(":");
                if (resource == null && parts.length > 0) {
                    resource = parts[0];
                }
                if (action == null && parts.length > 1) {
                    action = parts[1];
                }
            } else if (resource != null && action != null) {
                this.setName(resource + ":" + action);
            }
        }
    }

    @PreUpdate
    protected void onRemove() {
        for (Role role : roles) {
            role.getPermissions().remove(this);
        }
        roles.clear();
    }
}
