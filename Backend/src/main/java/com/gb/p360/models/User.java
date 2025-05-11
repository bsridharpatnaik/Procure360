package com.gb.p360.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Audited
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 120)
  @JsonIgnore
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @ManyToMany(fetch = FetchType.LAZY)
  @JsonIgnoreProperties({"users", "procurementRequests", "lineItems"})
  @JsonBackReference("factory-users")
  @JoinTable(
          name = "user_factory",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "factory_id")
  )
  private Set<Factory> factories = new HashSet<>();

  @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonIgnoreProperties({"createdBy", "owner", "factory", "lineItems"})
  private Set<ProcurementRequest> createdRequests = new HashSet<>();

  @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonIgnoreProperties({"createdBy", "owner", "factory", "lineItems"})
  private Set<ProcurementRequest> ownedRequests = new HashSet<>();

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