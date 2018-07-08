package com.convrt.service;

import com.convrt.entity.Playlist;
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

    @Transactional
    public User createUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists for email address " + user.getEmail());
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
        if (pin == null || email == null) {
            throw new RuntimeException("Must provide both pin and email address");
        }
        User user = userRepository.findByPinAndEmail(pin, email);
        if (user == null) {
            throw new RuntimeException("User pin and/or email not found");
        }
        return user;
    }

    @Transactional
    public User updatePlaylistList(Playlist playlist) {

    }

    @Transactional
    public User readPlaylistList(Playlist playlist) {

    }

}
