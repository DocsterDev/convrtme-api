package com.convrt.service;

import com.convrt.entity.User;
import com.convrt.repository.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> readUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User readUser(String uuid) {
        User user = userRepository.findOne(uuid);
        if (user == null) {
            throw new RuntimeException("User not found uuid=" + uuid);
        }
        return user;
    }

    @Transactional
    public User createUser(User user) {
        String email = user.getEmail();
        if (existsByEmail(email)) {
            throw new RuntimeException("User already exists for email address " + email);
        }
        user.setUuid(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getUserByPinAndEmail(String pin, String email) {
        User user = userRepository.findByPinAndEmail(pin, email);
        if (user == null) {
            throw new RuntimeException("User pin and/or email not found");
        }
        return user;
    }

}
