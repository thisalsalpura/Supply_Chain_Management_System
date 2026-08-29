package com.thisal.supply_chain_core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(columnList = "token", unique = true),
                @Index(columnList = "username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "RefreshToken.findByToken", query = "SELECT rt FROM RefreshToken rt WHERE rt.token=:token"),
        @NamedQuery(name = "RefreshToken.deleteByToken", query = "DELETE FROM RefreshToken rt WHERE rt.token=:token"),
        @NamedQuery(name = "RefreshToken.deleteExpired", query = "DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID _id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false, length = 150)
    private String username;

    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

}