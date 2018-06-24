package com.convrt.service;

import com.convrt.entity.User;
import com.convrt.repository.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@NoArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> readUsers() {
        return userRepository.findAll();
    }

    public User readUser(@NotNull String uuid) {
        return userRepository.findOne(uuid);
    }

    public User createUser(@NotNull User user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

}
