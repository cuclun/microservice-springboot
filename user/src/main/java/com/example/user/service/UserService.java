package com.example.user.service;

import com.example.user.Repository.RoleRepository;
import com.example.user.Repository.UserRepository;
import com.example.user.dto.request.SignInRequest;
import com.example.user.dto.request.SignUpRequest;
import com.example.user.mapper.UserMapper;
import com.example.user.model.Role;
import com.example.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    public boolean save(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!!!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!!!");
        }

        User user = userMapper.signUpRequestToUser(signUpRequest);

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ROLE_USER").orElseThrow());

        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRoles(roles);

        userRepository.save(user);

        return true;
    }

    public User authentication(SignInRequest signInRequest) {
        User user =
                userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new RuntimeException(
                        "User khong ton tai"));
        if(user != null && !encoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mat khau");
        }
        return user;
    }
}