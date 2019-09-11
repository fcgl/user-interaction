package com.fcgl.madrid.dev;

import com.fcgl.madrid.user.dataModel.AuthProvider;
import com.fcgl.madrid.user.dataModel.User;
import com.fcgl.madrid.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataPopulation {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataPopulation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostConstruct
    public void init() {
        User user = new User();
        user.setName("FCGL TEST");
        user.setEmail("test@fcgl.io");
        user.setPassword("password");
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);
    }
}
