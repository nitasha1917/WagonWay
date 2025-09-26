package com.database.repositories;

import com.database.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByIsActive(Boolean isActive);

    @Query("{'name': {'$regex': ?0, '$options': 'i'}}")
    List<User> findByNameContainingIgnoreCase(String name);

    @Query("{'role': ?0, 'isActive': ?1}")
    List<User> findByRoleAndIsActive(String role, Boolean isActive);

    @Query("{'email': {'$regex': ?0, '$options': 'i'}}")
    List<User> findByEmailContainingIgnoreCase(String email);

    // Count queries
    Long countByRole(String role);

    Long countByIsActive(Boolean isActive);

    @Query(value = "{'createdAt': {'$gte': ?0, '$lte': ?1}}", count = true)
    Long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
