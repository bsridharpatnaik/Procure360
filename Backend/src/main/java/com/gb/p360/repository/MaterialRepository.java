package com.gb.p360.repository;

import com.gb.p360.models.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByNameAndUnitOfMeasure(String name, String unitOfMeasure);

    @Query("SELECT m FROM Material m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Material> searchByNameContaining(String query);
}