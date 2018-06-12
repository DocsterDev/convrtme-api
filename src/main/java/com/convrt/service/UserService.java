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

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User addNewUser(@NotNull User newUser) {
        if (!userRepository.existsByEmail(newUser.getEmail())) {
            return userRepository.save(newUser);
        }

        return null;
    }


}
