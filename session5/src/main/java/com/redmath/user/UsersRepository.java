package com.redmath.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    // Method to find a user by username
    Optional<Users> findByUsername(String username);

    // Method to check if a user exists by username
    boolean existsByUsername(String username);

    // Method to delete a user by userId
    void deleteByUserId(Long userId);
}
