package com.gb.p360.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "factories")
public class Factory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "factory", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"factory", "createdBy", "owner", "lineItems"})
    @JsonManagedReference("factory-procurementRequests")
    private Set<ProcurementRequest> procurementRequests = new HashSet<>();

    @ManyToMany(mappedBy = "factories", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"factories", "createdRequests", "ownedRequests", "password"})
    @JsonManagedReference("factory-users")
    private Set<User> users = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}