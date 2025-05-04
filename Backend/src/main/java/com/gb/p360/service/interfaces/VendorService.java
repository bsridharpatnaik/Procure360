package com.gb.p360.service.interfaces;

import com.gb.p360.models.PriceHistory;
import com.gb.p360.models.Vendor;

import java.util.List;

public interface VendorService {
    Vendor findOrCreateVendor(String name);

    List<Vendor> searchVendors(String query);

    List<Vendor> getAllVendors();

    Vendor getVendorById(Long id);

    List<PriceHistory> getMaterialPriceHistory(Long materialId, Long vendorId);
}