package com.thisal.supply_chain_core.entity;

import com.thisal.supply_chain_core.enums.VendorStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "Vendor.findAll", query = "SELECT v FROM Vendor v"),
        @NamedQuery(name = "Vendor.findByEmail", query = "SELECT v FROM Vendor v WHERE v.email=:email")
})
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID _id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_status", nullable = false, length = 20)
    private VendorStatus vendorStatus;

    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.vendorStatus == null) this.vendorStatus = VendorStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

}