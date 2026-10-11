package com.example.sportspace.controller;
import com.example.sportspace.entity.Venue; import com.example.sportspace.service.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.List;
import org.springframework.format.annotation.DateTimeFormat; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/venues")
public class VenueController {
    public record VenueRequest(@NotBlank(message = "Name is required") String name, @NotBlank(message = "Location is required") String location,
                               @NotNull(message = "Sport is required") Long sportId,
                               @NotNull(message = "Venue type is required") @Pattern(regexp = "INDOOR|OUTDOOR", message = "Venue type must be INDOOR or OUTDOOR") String venueType,
                               @NotNull(message = "Price is required") @Positive(message = "Price must be positive") BigDecimal pricePerSlot) {}
    private final VenueService service; private final SportService sports; private final BookingService bookings; private final UserService users;
    public VenueController(VenueService s, SportService sp, BookingService b, UserService u) { service = s; sports = sp; bookings = b; users = u; }
    @GetMapping public List<Venue> all() { return service.all(); }
    @GetMapping("/{id}") public Venue one(@PathVariable Long id) { return service.find(id); }
    @GetMapping("/{id}/availability")
    public List<BookingService.SlotAvailability> availability(@PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) { return bookings.availability(id, date); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Venue add(@RequestHeader(value = "X-User-Id", required = false) Long uid, @Valid @RequestBody VenueRequest r) {
        users.requireAdmin(uid); return service.add(new Venue(r.name(), r.location(), sports.find(r.sportId()), r.venueType(), r.pricePerSlot()));
    }
    @PutMapping("/{id}")
    public Venue update(@RequestHeader(value = "X-User-Id", required = false) Long uid, @PathVariable Long id, @Valid @RequestBody VenueRequest r) {
        users.requireAdmin(uid); return service.update(id, new Venue(r.name(), r.location(), sports.find(r.sportId()), r.venueType(), r.pricePerSlot()));
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@RequestHeader(value = "X-User-Id", required = false) Long uid, @PathVariable Long id) { users.requireAdmin(uid); service.archive(id); }
}
