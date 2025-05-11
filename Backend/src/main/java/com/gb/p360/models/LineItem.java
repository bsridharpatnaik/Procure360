package com.gb.p360.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Audited
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "line_items")
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "procurement_request_id", nullable = false)
    @JsonIgnoreProperties({"lineItems", "factory", "createdBy", "owner"})
    private ProcurementRequest procurementRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private String unit;

    @Column(name = "requested_quantity", nullable = false)
    private BigDecimal requestedQuantity;

    @Column(name = "ordered_quantity")
    private BigDecimal orderedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LineItemStatus status;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "total_order_value")
    private BigDecimal totalOrderValue;

    @Column(name = "purchase_team_remarks", columnDefinition = "TEXT")
    private String purchaseTeamRemarks;

    @Column(name = "factory_remarks", columnDefinition = "TEXT")
    private String factoryRemarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status_changed_at")
    private LocalDateTime statusChangedAt;

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