package com.convrt.repository;

import com.convrt.entity.Auth;
import com.convrt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, String> {

    Auth findByToken(String token);

    Auth findByUserAgentAndUserUuid(String userAgent, String userUuid);

    Auth findByUser(User user);

    Auth findByUserAndUserAgent(User user, String userAgent);

}
