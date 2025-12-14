package org.boomer.gameserver.services;

import org.boomer.gameserver.entities.User;
import org.boomer.gameserver.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {
    private final UserRepository userRepository;

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByUsernameAndPassword(String username, String password) {
        var user = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }
}
