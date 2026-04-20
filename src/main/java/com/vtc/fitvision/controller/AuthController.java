package com.vtc.fitvision.controller;

import com.vtc.fitvision.model.User;
import com.vtc.fitvision.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody User user) {
        return ResponseEntity.ok(authService.googleLogin(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            String token = authService.register(user);
            return ResponseEntity.ok(token);

        } catch (RuntimeException e) {

            if (e.getMessage().equals("EMAIL_EXISTS")) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            return ResponseEntity.status(500).body("Error Your email is already registered");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String token = authService.login(user.getEmail(), user.getPassword());
            return ResponseEntity.ok(token);

        } catch (RuntimeException e) {

            if (e.getMessage().equals("USER_NOT_FOUND")) {
                return ResponseEntity.badRequest().body("User not found");
            }

            if (e.getMessage().equals("INVALID_PASSWORD")) {
                return ResponseEntity.badRequest().body("Wrong password");
            }

            return ResponseEntity.status(500).body("Error");
        }
    }
}