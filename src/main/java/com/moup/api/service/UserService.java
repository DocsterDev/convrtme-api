package com.moup.api.service;

import com.moup.api.repository.UserRepository;
import com.moup.api.entity.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaylistService playlistService;

    @Transactional
    public User createUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists for email address " + user.getEmail());
        }
        user.setUuid(UUID.randomUUID().toString());
        // user.setPlaylists(Lists.newArrayList());
        //playlistService.generatePlaylists(user).forEach((e) -> user.getPlaylists().add(e));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getUserByPinAndEmail(String pin, String email) {
        if (pin == null || email == null) {
            throw new RuntimeException("Must provide both pin and email address");
        }
        User user = userRepository.findByPinAndEmail(pin, email);
        if (user == null) {
            throw new RuntimeException("User pin and/or email not found");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User readUser(String userUuid) {
        // User user = userRepository.findOne(userUuid);
        Optional<User> user = userRepository.findById(userUuid);
        if (!user.isPresent()) {
            throw new RuntimeException(String.format("User not found uuid %s", userUuid));
        }
        return user.get();
    }

    @Transactional
    public User updateUser(User user) {
        // User userPersistent = userRepository.findOne(user.getUuid());
        Optional<User> userPersistent = userRepository.findById(user.getUuid());
        if (!userPersistent.isPresent()) {
            throw new RuntimeException(String.format("User not found to update uuid %s", user.getUuid()));
        }
        return userRepository.save(user);
    }

}
