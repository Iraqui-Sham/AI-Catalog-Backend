package com.vtc.fitvision.service;

import com.vtc.fitvision.model.User;
import com.vtc.fitvision.repository.UserRepository;
import com.vtc.fitvision.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;
    
    public Map<String, Object> googleLogin(User user) {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        User dbUser;

        if (existingUser.isPresent()) {
            dbUser = existingUser.get();
        } else {
            user.setCredits(100);
            dbUser = userRepository.save(user);
        }

        String token = jwtUtil.generateToken(dbUser.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", dbUser);

        return response;
    }

    public String register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setCredits(100);

        userRepository.save(user);

        return jwtUtil.generateToken(user.getEmail());
    }

    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(email);
    }
}