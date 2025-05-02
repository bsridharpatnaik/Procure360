package com.gb.p360.service;

import com.gb.p360.data.FileDownloadInfo;
import com.gb.p360.data.FileInfo;
import com.gb.p360.models.FileEntity;
import com.gb.p360.repository.FileRepository;
import com.gb.p360.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
public class FileHandlingService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Transactional
    public FileInfo uploadFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        byte[] data = file.getBytes();
        UUID fileUuid = UUID.randomUUID();
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileUuid(fileUuid.toString());
        fileEntity.setFilename(filename);
        fileEntity.setData(data);
        fileEntity.setUploadDate(new Date());
        fileRepository.save(fileEntity);
        return new FileInfo(fileEntity);
    }

    public FileDownloadInfo downloadFile(String fileUuid) throws IOException {
        FileEntity fileEntity = fileRepository.findByFileUuid(fileUuid);
        if (fileEntity == null) {
            throw new IOException("File not found");
        }
        return new FileDownloadInfo(fileEntity.getFilename(), fileEntity.getData());
    }
}