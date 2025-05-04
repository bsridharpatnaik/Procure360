package com.gb.p360.repository;

import com.gb.p360.models.Factory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactoryRepository extends JpaRepository<Factory, Long> {
    Optional<Factory> findByName(String name);
    Optional<Factory> findByCode(String code);
}