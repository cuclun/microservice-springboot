package com.example.user.controller;

import com.example.user.dto.request.SignInRequest;
import com.example.user.model.User;
import com.example.user.security.jwt.JwtUtils;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    private final JwtUtils jwtUtils;

    @PostMapping(path = {"/signin", "/signin/"})
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        try {
            User user = userService.authentication(signInRequest);
            String jwt = jwtUtils.generateJwtToken(user);

            return ResponseEntity.ok(Map.of("accessToken", jwt, "username", user.getUsername(), "roles", user.getRoles()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }
}
