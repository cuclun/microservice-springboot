package com.example.user.controller;

import com.example.user.dto.request.SignInRequest;
import com.example.user.dto.request.SignUpRequest;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

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

    @PostMapping(path = {"/signup/", "/signup"})
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            return ResponseEntity.ok(userService.save(signUpRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

    @GetMapping(path = "/admin")
    public boolean admin() {
        return true;
    }
}
