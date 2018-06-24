package com.convrt.service;

import com.convrt.entity.User;
import com.convrt.repository.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

}
