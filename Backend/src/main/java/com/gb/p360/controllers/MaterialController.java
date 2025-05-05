package com.gb.p360.controllers;

import com.gb.p360.models.Material;
import com.gb.p360.service.interfaces.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@Tag(name = "Material API", description = "APIs for managing materials")
public class MaterialController {

    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    @Operation(summary = "Get all materials", description = "Returns a list of all materials in the system")
    public ResponseEntity<List<Material>> getAllMaterials() {
        List<Material> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get material by ID", description = "Returns a material based on the ID provided")
    public ResponseEntity<Material> getMaterialById(@PathVariable Long id) {
        Material material = materialService.getMaterialById(id);
        return ResponseEntity.ok(material);
    }

    @GetMapping("/search")
    @Operation(summary = "Search materials by name", description = "Returns materials matching the search query")
    public ResponseEntity<List<Material>> searchMaterials(@RequestParam String query) {
        List<Material> materials = materialService.searchMaterials(query);
        return ResponseEntity.ok(materials);
    }
}
