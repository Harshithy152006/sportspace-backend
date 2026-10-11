package com.example.sportspace.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.*;
@Entity
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private User user; @ManyToOne(optional = false) private Venue venue; @ManyToOne(optional = false) private TimeSlot slot;
    private LocalDate bookingDate; private BigDecimal price;
    private String status = "PENDING"; private LocalDateTime createdAt = LocalDateTime.now();
    @Column(length=12) private String paymentMethod = "CASH";
    @Column(length=24) private String paymentStatus = "PENDING";
    @Column(length=30) private String paymentReference;
    private LocalDateTime paymentUpdatedAt;
    private LocalDateTime paymentExpiresAt;
    public Booking() {}
    public Booking(User u, Venue v, TimeSlot s, LocalDate d) { user = u; venue = v; slot = s; bookingDate = d; price = v.getPricePerSlot(); }
    public Long getId() { return id; } public User getUser() { return user; } public Venue getVenue() { return venue; } public TimeSlot getSlot() { return slot; }
    public LocalDate getBookingDate() { return bookingDate; } public BigDecimal getPrice() { return price; }
    public String getStatus() { return status; } public void setStatus(String s) { status = s; } public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPaymentMethod() { return paymentMethod == null ? "CASH" : paymentMethod; }
    public String getPaymentStatus() { return paymentStatus == null ? "PENDING" : paymentStatus; }
    public String getPaymentReference() { return paymentReference; }
    public LocalDateTime getPaymentUpdatedAt() { return paymentUpdatedAt; }
    public LocalDateTime getPaymentExpiresAt() { return paymentExpiresAt; }
    public void setPaymentMethod(String value) { paymentMethod = value; }
    public void setPaymentStatus(String value) { paymentStatus = value; }
    public void setPaymentReference(String value) { paymentReference = value; }
    public void setPaymentUpdatedAt(LocalDateTime value) { paymentUpdatedAt = value; }
    public void setPaymentExpiresAt(LocalDateTime value) { paymentExpiresAt = value; }
}
