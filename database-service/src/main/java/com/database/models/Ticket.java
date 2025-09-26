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

@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;

    @Field("user_id")
    @NotBlank(message = "User ID is required")
    @Indexed
    private String userId;

    @Field("event_id")
    @NotBlank(message = "Event ID is required")
    @Indexed
    private String eventId;

    @Field("ticket_number")
    @NotBlank(message = "Ticket number is required")
    @Indexed(unique = true)
    private String ticketNumber;

    @Field("quantity")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Field("total_amount")
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal totalAmount;

    @Field("booking_status")
    @Indexed
    private String bookingStatus = "PENDING"; // PENDING, CONFIRMED, CANCELLED

    @Field("payment_status")
    @Indexed
    private String paymentStatus = "PENDING"; // PENDING, COMPLETED, FAILED, REFUNDED

    @Field("seat_numbers")
    private String seatNumbers; // "A1,A2,A3"

    @Field("seat_type")
    private String seatType = "REGULAR"; // REGULAR, PREMIUM, VIP

    @Field("qr_code")
    private String qrCode;

    @Field("booking_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingTime;

    @Field("payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentTime;

    @Field("cancellation_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancellationTime;

    @Field("cancellation_reason")
    private String cancellationReason;

    @Field("payment_method")
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING

    @Field("transaction_id")
    private String transactionId;

    @Field("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public Ticket() {
        this.bookingTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Ticket(String userId, String eventId, Integer quantity, BigDecimal totalAmount) {
        this();
        this.userId = userId;
        this.eventId = eventId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) {
        this.userId = userId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) {
        this.eventId = eventId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.updatedAt = LocalDateTime.now();
        if ("COMPLETED".equals(paymentStatus)) {
            this.paymentTime = LocalDateTime.now();
        }
    }

    public String getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(String seatNumbers) {
        this.seatNumbers = seatNumbers;
        this.updatedAt = LocalDateTime.now();
    }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) {
        this.seatType = seatType;
        this.updatedAt = LocalDateTime.now();
    }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }

    public LocalDateTime getCancellationTime() { return cancellationTime; }
    public void setCancellationTime(LocalDateTime cancellationTime) { this.cancellationTime = cancellationTime; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
