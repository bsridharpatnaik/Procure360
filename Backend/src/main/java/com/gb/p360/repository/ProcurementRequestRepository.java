package com.gb.p360.repository;

import com.gb.p360.models.Factory;
import com.gb.p360.models.ProcurementRequest;
import com.gb.p360.models.RequestStatus;
import com.gb.p360.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcurementRequestRepository extends JpaRepository<ProcurementRequest, Long> {
    List<ProcurementRequest> findByFactory(Factory factory);
    List<ProcurementRequest> findByFactoryAndStatus(Factory factory, RequestStatus status);
    List<ProcurementRequest> findByCreatedBy(User user);
    List<ProcurementRequest> findByOwner(User owner);

    @Query("SELECT MAX(CAST(SUBSTRING(pr.uniqueIdentifier, LENGTH(CONCAT(:factoryCode, '-')) + 1) AS int)) " +
            "FROM ProcurementRequest pr " +
            "WHERE pr.uniqueIdentifier LIKE CONCAT(:factoryCode, '-%')")
    Optional<Integer> findMaxRequestNumberByFactoryCode(String factoryCode);

    List<ProcurementRequest> findByFactoryAndCreatedAtAfter(Factory factory, LocalDateTime timestamp);

    Page<ProcurementRequest> findByFactoryInAndStatus(List<Factory> factories, RequestStatus status, Pageable pageable);
}
