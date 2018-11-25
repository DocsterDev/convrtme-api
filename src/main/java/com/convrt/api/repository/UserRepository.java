package com.convrt.api.repository;

import com.convrt.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    User findByPinAndEmail(String pin, String email);
}
