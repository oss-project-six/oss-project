package com.example.oss_project.repository.user;

import com.example.oss_project.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public UserRepository(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }
}
