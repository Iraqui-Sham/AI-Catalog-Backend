package com.vtc.fitvision.controller;

import com.vtc.fitvision.service.AIService;
import com.vtc.fitvision.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam("file") MultipartFile file) {

        try {

            // 🔴 STEP 1: Token check
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token missing ❌");
            }

            // 🔴 STEP 2: Bearer format check
            if (!token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token format ❌");
            }

            // 🔐 STEP 3: Clean token
            String cleanToken = token.replace("Bearer ", "");

            // 🔍 STEP 4: Validate token
            String email = jwtUtil.extractEmail(cleanToken);

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token ❌");
            }

            // ✅ USER VERIFIED
            System.out.println("User: " + email);

            // 🔥 AI CALL
            String result = aiService.processImage(file);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Invalid or expired token ❌");
        }
    }
}