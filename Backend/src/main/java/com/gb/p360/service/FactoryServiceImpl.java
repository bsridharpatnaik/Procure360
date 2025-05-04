package com.gb.p360.service;

import java.util.List;

import com.gb.p360.data.FactoryDTO;
import com.gb.p360.exception.ResourceNotFoundException;
import com.gb.p360.models.Factory;
import com.gb.p360.repository.FactoryRepository;
import com.gb.p360.service.interfaces.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FactoryServiceImpl implements FactoryService {

    private final FactoryRepository factoryRepository;

    @Autowired
    public FactoryServiceImpl(FactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    @Override
    public Factory createFactory(FactoryDTO factoryDTO) {
        Factory factory = new Factory();
        factory.setName(factoryDTO.getName());
        factory.setCode(factoryDTO.getCode());
        return factoryRepository.save(factory);
    }

    @Override
    public Factory updateFactory(Long id, FactoryDTO factoryDTO) {
        Factory factory = factoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with id: " + id));

        factory.setName(factoryDTO.getName());
        factory.setCode(factoryDTO.getCode());
        return factoryRepository.save(factory);
    }

    @Override
    public Factory getFactoryById(Long id) {
        return factoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with id: " + id));
    }

    @Override
    public Factory getFactoryByName(String name) {
        return factoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with name: " + name));
    }

    @Override
    public Factory getFactoryByCode(String code) {
        return factoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with code: " + code));
    }

    @Override
    public List<Factory> getAllFactories() {
        return factoryRepository.findAll();
    }

    @Override
    public void deleteFactory(Long id) {
        if (!factoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Factory not found with id: " + id);
        }
        factoryRepository.deleteById(id);
    }
}