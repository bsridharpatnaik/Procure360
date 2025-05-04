package com.gb.p360.service;

import com.gb.p360.exception.ResourceNotFoundException;
import com.gb.p360.models.Material;
import com.gb.p360.repository.MaterialRepository;
import com.gb.p360.service.interfaces.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public Material findOrCreateMaterial(String name, String unitOfMeasure) {
        Optional<Material> existingMaterial = materialRepository.findByNameAndUnitOfMeasure(name, unitOfMeasure);

        if (existingMaterial.isPresent()) {
            return existingMaterial.get();
        } else {
            Material newMaterial = new Material();
            newMaterial.setName(name);
            newMaterial.setUnitOfMeasure(unitOfMeasure);
            return materialRepository.save(newMaterial);
        }
    }

    @Override
    public List<Material> searchMaterials(String query) {
        return materialRepository.searchByNameContaining(query);
    }

    @Override
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Override
    public Material getMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));
    }
}