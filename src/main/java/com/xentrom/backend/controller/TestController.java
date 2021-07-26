package com.xentrom.backend.controller;

import com.xentrom.backend.dto.output.Output;
import com.xentrom.backend.dto.output.RegisterOutput;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    @GetMapping(value = "/ping")
    public ResponseEntity<String> registerUser() {
        return ResponseEntity
                .ok()
                .body("PONG");
    }
}
