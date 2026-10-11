package com.example.sportspace.controller;
import com.example.sportspace.entity.Booking; import com.example.sportspace.service.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.List;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/bookings")
public class BookingController {
    public record BookingRequest(@NotNull(message = "Venue is required") Long venueId, @NotNull(message = "Time slot is required") Long slotId,
        @NotNull(message = "Date is required") @FutureOrPresent(message = "Date cannot be in the past") LocalDate bookingDate,
        @NotBlank(message="Choose a payment method") String paymentMethod) {}
    public record ReferenceRequest(@NotBlank(message="Transaction reference is required") String reference) {}
    private final BookingService service; private final UserService users;
    public BookingController(BookingService s, UserService u) { service = s; users = u; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestHeader(value="X-User-Id",required=false) Long uid, @Valid @RequestBody BookingRequest r) { return service.create(users.requireUser(uid),r.venueId(),r.slotId(),r.bookingDate(),r.paymentMethod()); }
    @GetMapping("/my") public List<Booking> mine(@RequestHeader(value="X-User-Id",required=false) Long uid) { return service.mine(users.requireUser(uid).getId()); }
    @GetMapping public List<Booking> all(@RequestHeader(value="X-User-Id",required=false) Long uid) { users.requireAdmin(uid); return service.all(); }
    @PatchMapping("/{id}/{action}") public Booking change(@RequestHeader(value="X-User-Id",required=false) Long uid,@PathVariable Long id,@PathVariable String action) { return service.change(users.requireUser(uid),id,action); }
    @PatchMapping("/{id}/payment/reference") public Booking reference(@RequestHeader(value="X-User-Id",required=false) Long uid,@PathVariable Long id,@Valid @RequestBody ReferenceRequest r) { return service.submitReference(users.requireUser(uid),id,r.reference()); }
    @PatchMapping("/{id}/payment/verify") public Booking verify(@RequestHeader(value="X-User-Id",required=false) Long uid,@PathVariable Long id) { return service.verify(users.requireAdmin(uid),id); }
    @PatchMapping("/{id}/payment/reject") public Booking rejectPayment(@RequestHeader(value="X-User-Id",required=false) Long uid,@PathVariable Long id) { return service.rejectPayment(users.requireAdmin(uid),id); }
    @PatchMapping("/{id}/payment/cash-received") public Booking cashReceived(@RequestHeader(value="X-User-Id",required=false) Long uid,@PathVariable Long id) { return service.cashReceived(users.requireAdmin(uid),id); }
}
