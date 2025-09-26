package com.database.repositories;

import com.database.models.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    List<Ticket> findByUserId(String userId);

    List<Ticket> findByEventId(String eventId);

    List<Ticket> findByUserIdAndBookingStatus(String userId, String status);

    List<Ticket> findByEventIdAndBookingStatus(String eventId, String status);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByBookingStatus(String status);

    List<Ticket> findByPaymentStatus(String status);

    @Query("{'userId': ?0, 'bookingStatus': {'$in': ['CONFIRMED', 'PENDING']}}")
    List<Ticket> findActiveBookingsByUserId(String userId);

    @Query("{'eventId': ?0, 'bookingStatus': 'CONFIRMED'}")
    List<Ticket> findConfirmedBookingsByEventId(String eventId);

    @Query("{'bookingTime': {'$gte': ?0, '$lte': ?1}}")
    List<Ticket> findByBookingTimeBetween(LocalDateTime start, LocalDateTime end);

    // Count queries
    Long countByEventIdAndBookingStatus(String eventId, String status);

    Long countByUserIdAndBookingStatus(String userId, String status);

    Long countByBookingStatus(String status);

    @Query(value = "{'eventId': ?0, 'bookingStatus': 'CONFIRMED'}", count = true)
    Long countConfirmedTicketsByEventId(String eventId);

    // Revenue calculations
    @Query("{'bookingStatus': 'CONFIRMED', 'paymentStatus': 'COMPLETED'}")
    List<Ticket> findCompletedBookings();

    @Query("{'eventId': ?0, 'bookingStatus': 'CONFIRMED', 'paymentStatus': 'COMPLETED'}")
    List<Ticket> findCompletedBookingsByEventId(String eventId);

    @Query("{'userId': ?0, 'bookingTime': {'$gte': ?1, '$lte': ?2}}")
    List<Ticket> findUserBookingsBetweenDates(String userId, LocalDateTime start, LocalDateTime end);

    // Analytics queries
    @Query(value = "{'bookingTime': {'$gte': ?0, '$lte': ?1}, 'bookingStatus': 'CONFIRMED'}", count = true)
    Long countConfirmedBookingsBetween(LocalDateTime start, LocalDateTime end);

    @Query("{'$group': {'_id': '$eventId', 'totalBookings': {'$sum': 1}, 'totalRevenue': {'$sum': '$totalAmount'}}}")
    List<Object> getEventBookingStats();
}
