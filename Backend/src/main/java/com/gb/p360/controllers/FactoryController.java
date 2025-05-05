package com.gb.p360.controllers;

import com.gb.p360.data.FactoryDTO;
import com.gb.p360.models.Factory;
import com.gb.p360.service.interfaces.FactoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factories")
@Tag(name = "Factory Management API", description = "APIs for managing factories")
public class FactoryController {

    private final FactoryService factoryService;

    @Autowired
    public FactoryController(FactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new factory", description = "Creates a new factory in the system")
    public ResponseEntity<Factory> createFactory(@RequestBody FactoryDTO factoryDTO) {
        Factory createdFactory = factoryService.createFactory(factoryDTO);
        return new ResponseEntity<>(createdFactory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing factory", description = "Updates a factory's details")
    public ResponseEntity<Factory> updateFactory(@PathVariable Long id, @RequestBody FactoryDTO factoryDTO) {
        Factory updatedFactory = factoryService.updateFactory(id, factoryDTO);
        return ResponseEntity.ok(updatedFactory);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get factory by ID", description = "Returns a factory based on the ID provided")
    public ResponseEntity<Factory> getFactoryById(@PathVariable Long id) {
        Factory factory = factoryService.getFactoryById(id);
        return ResponseEntity.ok(factory);
    }

    @GetMapping
    @Operation(summary = "Get all factories", description = "Returns a list of all factories in the system")
    public ResponseEntity<List<Factory>> getAllFactories() {
        List<Factory> factories = factoryService.getAllFactories();
        return ResponseEntity.ok(factories);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get factory by name", description = "Returns a factory based on the name provided")
    public ResponseEntity<Factory> getFactoryByName(@PathVariable String name) {
        Factory factory = factoryService.getFactoryByName(name);
        return ResponseEntity.ok(factory);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get factory by code", description = "Returns a factory based on the code provided")
    public ResponseEntity<Factory> getFactoryByCode(@PathVariable String code) {
        Factory factory = factoryService.getFactoryByCode(code);
        return ResponseEntity.ok(factory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a factory", description = "Removes a factory from the system")
    public ResponseEntity<Void> deleteFactory(@PathVariable Long id) {
        factoryService.deleteFactory(id);
        return ResponseEntity.noContent().build();
    }
}