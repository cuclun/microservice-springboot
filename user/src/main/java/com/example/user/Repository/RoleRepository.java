package com.example.user.Repository;


import java.util.Optional;

import com.example.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByName(String roleName);

    Optional<Role> findByName(String roleName);
}