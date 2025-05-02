package com.gb.p360.repository;

import com.gb.p360.models.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    @Transactional
    void deleteByTimestampBefore(Date cutoffDate);
}