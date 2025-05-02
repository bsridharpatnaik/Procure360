package com.gb.p360.controllers;

import com.gb.p360.data.FileDownloadInfo;
import com.gb.p360.data.FileInfo;
import com.gb.p360.service.FileHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileHandlingController {

    @Autowired
    private FileHandlingService fileHandlingService;

    @PostMapping("/upload")
    public ResponseEntity<FileInfo> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        FileInfo fileInfo = fileHandlingService.uploadFile(file);
        return ResponseEntity.ok(fileInfo);
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String uuid) throws IOException {
        FileDownloadInfo fileDownloadInfo = fileHandlingService.downloadFile(uuid);

        // Set headers to force download with the correct filename
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileDownloadInfo.getFilename());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Return the file with the original filename
        return new ResponseEntity<>(fileDownloadInfo.getFileData(), headers, HttpStatus.OK);
    }
}
