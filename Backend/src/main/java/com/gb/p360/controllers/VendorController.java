package com.gb.p360.controllers;

import com.gb.p360.models.PriceHistory;
import com.gb.p360.models.Vendor;
import com.gb.p360.service.interfaces.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@Tag(name = "Vendor API", description = "APIs for managing vendors")
public class VendorController {

    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    @Operation(summary = "Get all vendors", description = "Returns a list of all vendors in the system")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vendor by ID", description = "Returns a vendor based on the ID provided")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        Vendor vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(vendor);
    }

    @GetMapping("/search")
    @Operation(summary = "Search vendors by name", description = "Returns vendors matching the search query")
    public ResponseEntity<List<Vendor>> searchVendors(@RequestParam String query) {
        List<Vendor> vendors = vendorService.searchVendors(query);
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/material/{materialId}/price-history")
    @Operation(summary = "Get price history for a material", description = "Returns price history entries for a material, optionally filtered by vendor")
    public ResponseEntity<List<PriceHistory>> getMaterialPriceHistory(
            @PathVariable Long materialId,
            @RequestParam(required = false) Long vendorId) {
        List<PriceHistory> priceHistory = vendorService.getMaterialPriceHistory(materialId, vendorId);
        return ResponseEntity.ok(priceHistory);
    }
}