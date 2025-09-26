package com.database.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "events")
public class Event {
    @Id
    private String id;

    @Field("event_name")
    @NotBlank(message = "Event name is required")
    @Indexed
    private String eventName;

    @Field("description")
    private String description;

    @Field("category")
    @NotBlank(message = "Category is required")
    @Indexed
    private String category; // MOVIE, CONCERT, SPORTS, THEATER

    @Field("poster_url")
    private String posterUrl;

    @Field("event_datetime")
    @NotNull(message = "Event date and time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDateTime;

    @Field("venue")
    @NotBlank(message = "Venue is required")
    private String venue;

    @Field("city")
    @NotBlank(message = "City is required")
    @Indexed
    private String city;

    @Field("ticket_price")
    @NotNull(message = "Ticket price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal ticketPrice;

    @Field("total_seats")
    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;

    @Field("available_seats")
    @NotNull(message = "Available seats is required")
    private Integer availableSeats;

    @Field("is_active")
    private Boolean isActive = true;

    @Field("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Field("created_by")
    private String createdBy; // Admin user ID

    // Additional fields
    @Field("duration")
    private Integer duration; // in minutes

    @Field("language")
    private String language;

    @Field("age_rating")
    private String ageRating; // U, UA, A, S

    @Field("tags")
    private String[] tags;

    // Constructors
    public Event() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Event(String eventName, String category, String venue, String city,
                 BigDecimal ticketPrice, Integer totalSeats, LocalDateTime eventDateTime) {
        this();
        this.eventName = eventName;
        this.category = category;
        this.venue = venue;
        this.city = city;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.eventDateTime = eventDateTime;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) {
        this.eventName = eventName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
        this.updatedAt = LocalDateTime.now();
    }

    public String getVenue() { return venue; }
    public void setVenue(String venue) {
        this.venue = venue;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCity() { return city; }
    public void setCity(String city) {
        this.city = city;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) {
        this.duration = duration;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) {
        this.language = language;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
        this.updatedAt = LocalDateTime.now();
    }

    public String[] getTags() { return tags; }
    public void setTags(String[] tags) {
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }
}
