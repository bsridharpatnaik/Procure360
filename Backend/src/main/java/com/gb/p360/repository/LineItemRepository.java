package com.gb.p360.repository;

import com.gb.p360.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Long> {
    List<LineItem> findByProcurementRequest(ProcurementRequest procurementRequest);
    List<LineItem> findByMaterial(Material material);
    List<LineItem> findByVendor(Vendor vendor);
    List<LineItem> findByStatus(LineItemStatus status);
    List<LineItem> findByProcurementRequestAndStatus(ProcurementRequest request, LineItemStatus status);
}
