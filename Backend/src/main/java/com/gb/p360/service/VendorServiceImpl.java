package com.gb.p360.service;

import com.gb.p360.exception.ResourceNotFoundException;
import com.gb.p360.models.Material;
import com.gb.p360.models.PriceHistory;
import com.gb.p360.models.Vendor;
import com.gb.p360.repository.MaterialRepository;
import com.gb.p360.repository.PriceHistoryRepository;
import com.gb.p360.repository.VendorRepository;
import com.gb.p360.service.interfaces.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final MaterialRepository materialRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Autowired
    public VendorServiceImpl(
            VendorRepository vendorRepository,
            MaterialRepository materialRepository,
            PriceHistoryRepository priceHistoryRepository) {
        this.vendorRepository = vendorRepository;
        this.materialRepository = materialRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    public Vendor findOrCreateVendor(String name) {
        Optional<Vendor> existingVendor = vendorRepository.findByName(name);

        if (existingVendor.isPresent()) {
            return existingVendor.get();
        } else {
            Vendor newVendor = new Vendor();
            newVendor.setName(name);
            return vendorRepository.save(newVendor);
        }
    }

    @Override
    public List<Vendor> searchVendors(String query) {
        return vendorRepository.searchByNameContaining(query);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
    }

    @Override
    public List<PriceHistory> getMaterialPriceHistory(Long materialId, Long vendorId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        // Limit to last 5 price entries
        Pageable limit = PageRequest.of(0, 5);

        if (vendorId != null) {
            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));

            return priceHistoryRepository.findByMaterialAndVendorOrderByOrderDateDesc(material, vendor, limit);
        } else {
            return priceHistoryRepository.findByMaterialOrderByOrderDateDesc(material, limit);
        }
    }
}