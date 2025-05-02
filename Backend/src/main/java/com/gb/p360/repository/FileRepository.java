package com.gb.p360.repository;

import com.gb.p360.models.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFileUuid(String fileUuid);
}
