package com.database.repositories;

import com.database.models.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    @Query("{'isActive': true, 'eventDateTime': {'$gte': ?0}}")
    List<Event> findActiveEventsAfterDate(LocalDateTime date);

    List<Event> findByCategory(String category);

    List<Event> findByCategoryAndIsActive(String category, Boolean isActive);

    List<Event> findByCityContainingIgnoreCase(String city);

    @Query("{'$or': [{'eventName': {'$regex': ?0, '$options': 'i'}}, {'description': {'$regex': ?0, '$options': 'i'}}], 'isActive': true}")
    List<Event> findByEventNameOrDescriptionContainingAndIsActive(String keyword);

    List<Event> findByCreatedBy(String createdBy);

    @Query("{'eventDateTime': {'$gte': ?0, '$lte': ?1}, 'isActive': true}")
    List<Event> findByEventDateTimeBetweenAndIsActive(LocalDateTime start, LocalDateTime end);

    @Query("{'availableSeats': {'$gt': 0}, 'isActive': true, 'eventDateTime': {'$gte': ?0}}")
    List<Event> findAvailableEvents(LocalDateTime currentTime);

    // Search with multiple criteria
    @Query("{'$and': [" +
            "{'$or': [" +
            "  {'eventName': {'$regex': ?0, '$options': 'i'}}," +
            "  {'description': {'$regex': ?0, '$options': 'i'}}" +
            "]}," +
            "{'category': {'$regex': ?1, '$options': 'i'}}," +
            "{'city': {'$regex': ?2, '$options': 'i'}}," +
            "{'isActive': true}" +
            "]}")
    List<Event> searchEvents(String keyword, String category, String city);

    // Count queries
    Long countByCategory(String category);

    Long countByIsActive(Boolean isActive);

    @Query(value = "{'createdAt': {'$gte': ?0, '$lte': ?1}}", count = true)
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Popular events (high booking ratio)
    @Query("{'availableSeats': {'$lt': {'$multiply': ['$totalSeats', 0.5]}}, 'isActive': true}")
    List<Event> findPopularEvents();
}
