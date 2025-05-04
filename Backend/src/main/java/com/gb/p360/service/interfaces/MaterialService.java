package com.gb.p360.service.interfaces;

import com.gb.p360.models.Material;

import java.util.List;

public interface MaterialService {
    Material findOrCreateMaterial(String name, String unitOfMeasure);
    List<Material> searchMaterials(String query);
    List<Material> getAllMaterials();
    Material getMaterialById(Long id);
}