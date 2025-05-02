package com.gb.p360.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/status")
    public ResponseEntity<String> getFolderContents(@RequestParam(required = false) Long folderId) {
        return ResponseEntity.ok("true");
    }
}
