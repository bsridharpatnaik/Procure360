package com.gb.p360.service.interfaces;

import com.gb.p360.data.FactoryDTO;
import com.gb.p360.models.Factory;

import java.util.List;

public interface FactoryService {
    Factory createFactory(FactoryDTO factoryDTO);

    Factory updateFactory(Long id, FactoryDTO factoryDTO);

    Factory getFactoryById(Long id);

    Factory getFactoryByName(String name);

    Factory getFactoryByCode(String code);

    List<Factory> getAllFactories();

    void deleteFactory(Long id);
}