package com.database.services;

import com.database.models.Event;
import com.database.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event) {
        // Set defaults
        if (event.getAvailableSeats() == null) {
            event.setAvailableSeats(event.getTotalSeats());
        }
        event.setIsActive(true);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    public Optional<Event> findById(String id) {
        return eventRepository.findById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getActiveEvents() {
        return eventRepository.findActiveEventsAfterDate(LocalDateTime.now());
    }

    public List<Event> getEventsByCategory(String category) {
        return eventRepository.findByCategoryAndIsActive(category, true);
    }

    public List<Event> getEventsByCity(String city) {
        return eventRepository.findByCityContainingIgnoreCase(city);
    }

    public List<Event> searchEvents(String keyword, String category, String city) {
        if ((keyword == null || keyword.trim().isEmpty()) &&
                (category == null || category.trim().isEmpty()) &&
                (city == null || city.trim().isEmpty())) {
            return getActiveEvents();
        }

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : "";
        String searchCategory = (category != null && !category.trim().isEmpty()) ? category.trim() : "";
        String searchCity = (city != null && !city.trim().isEmpty()) ? city.trim() : "";

        return eventRepository.searchEvents(searchKeyword, searchCategory, searchCity);
    }

    public List<Event> getAvailableEvents() {
        return eventRepository.findAvailableEvents(LocalDateTime.now());
    }

    public List<Event> getPopularEvents() {
        return eventRepository.findPopularEvents();
    }

    public Event updateEvent(String id, Event updatedEvent) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        Event event = existingEvent.get();

        // Update fields if provided
        if (updatedEvent.getEventName() != null) {
            event.setEventName(updatedEvent.getEventName());
        }
        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getPosterUrl() != null) {
            event.setPosterUrl(updatedEvent.getPosterUrl());
        }
        if (updatedEvent.getEventDateTime() != null) {
            event.setEventDateTime(updatedEvent.getEventDateTime());
        }
        if (updatedEvent.getVenue() != null) {
            event.setVenue(updatedEvent.getVenue());
        }
        if (updatedEvent.getCity() != null) {
            event.setCity(updatedEvent.getCity());
        }
        if (updatedEvent.getTicketPrice() != null) {
            event.setTicketPrice(updatedEvent.getTicketPrice());
        }
        if (updatedEvent.getDuration() != null) {
            event.setDuration(updatedEvent.getDuration());
        }
        if (updatedEvent.getLanguage() != null) {
            event.setLanguage(updatedEvent.getLanguage());
        }
        if (updatedEvent.getAgeRating() != null) {
            event.setAgeRating(updatedEvent.getAgeRating());
        }
        if (updatedEvent.getTags() != null) {
            event.setTags(updatedEvent.getTags());
        }

        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    public Event updateAvailableSeats(String id, Integer availableSeats) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        Event event = eventOpt.get();
        event.setAvailableSeats(availableSeats);
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    public void deactivateEvent(String id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        Event event = eventOpt.get();
        event.setIsActive(false);
        event.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(event);
    }

    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByEventDateTimeBetweenAndIsActive(start, end);
    }

    public List<Event> getEventsByCreator(String creatorId) {
        return eventRepository.findByCreatedBy(creatorId);
    }

    // Statistics
    public Long getTotalEvents() {
        return eventRepository.count();
    }

    public Long getActiveEventsCount() {
        return eventRepository.countByIsActive(true);
    }

    public Long getEventsByCategoryCount(String category) {
        return eventRepository.countByCategory(category);
    }

    public Long getNewEventsCount(LocalDateTime start, LocalDateTime end) {
        return eventRepository.countByCreatedAtBetween(start, end);
    }
}
