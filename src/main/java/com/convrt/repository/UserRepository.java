package com.convrt.repository;

import com.convrt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    User findByPinAndEmail(String pin, String email);

}
