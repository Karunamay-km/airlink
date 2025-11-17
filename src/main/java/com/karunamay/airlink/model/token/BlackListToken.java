package com.karunamay.airlink.model.token;

import com.karunamay.airlink.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Table(
    name = "blacklist_token",
    indexes = {
        @Index(name = "idx_blacklist_token_user", columnList = "user_id"),
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_blacklist_tokens_token",
            columnNames = "token"
        ),
    }
)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = { "user" })
public class BlackListToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Token id is required")
    @Column(name = "jti", nullable = false, updatable = false, unique = true)
    private String tokenId;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_blacklist_token_user")
    )
    private User user;

    @NotNull(message = "Expiry date is required")
    @Column(name = "expired_at", nullable = false)
    private LocalDate expiryDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
