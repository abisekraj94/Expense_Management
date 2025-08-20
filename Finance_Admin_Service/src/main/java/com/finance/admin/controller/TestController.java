package com.finance.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${test.health.message}")
    private String healthMessage;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(healthMessage);
    }
}