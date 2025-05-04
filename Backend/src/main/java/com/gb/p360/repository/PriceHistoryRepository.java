package com.gb.p360.repository;

import com.gb.p360.models.Material;
import com.gb.p360.models.PriceHistory;
import com.gb.p360.models.Vendor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByMaterialAndVendorOrderByOrderDateDesc(Material material, Vendor vendor, Pageable pageable);
    List<PriceHistory> findByMaterialOrderByOrderDateDesc(Material material, Pageable pageable);
}