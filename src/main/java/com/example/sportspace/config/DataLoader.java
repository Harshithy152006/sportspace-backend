package com.example.sportspace.config;
import com.example.sportspace.entity.*; import com.example.sportspace.repository.*;
import java.math.BigDecimal; import java.time.*;
import org.springframework.boot.CommandLineRunner; import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository users; private final SportRepository sports; private final TimeSlotRepository slots; private final VenueRepository venues; private final BookingRepository bookings;
    public DataLoader(UserRepository u, SportRepository s, TimeSlotRepository t, VenueRepository v, BookingRepository b) { users = u; sports = s; slots = t; venues = v; bookings = b; }
    // Runs once on an empty database so the app is usable immediately.
    public void run(String... args) {
        if (users.count() > 0) return;
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        users.save(new User("Admin", "admin@gmail.com", enc.encode("admin123"), "ADMIN"));
        User player = users.save(new User("Ravi", "ravi@gmail.com", enc.encode("user123"), "USER"));
        Sport football = sports.save(new Sport("Football")); Sport basketball = sports.save(new Sport("Basketball"));
        Sport tennis = sports.save(new Sport("Tennis")); Sport badminton = sports.save(new Sport("Badminton")); Sport cricket = sports.save(new Sport("Cricket"));
        TimeSlot morning = slots.save(new TimeSlot(LocalTime.of(6, 0), LocalTime.of(7, 0), true));
        slots.save(new TimeSlot(LocalTime.of(7, 0), LocalTime.of(8, 0), true));
        slots.save(new TimeSlot(LocalTime.of(17, 0), LocalTime.of(18, 0), true));
        slots.save(new TimeSlot(LocalTime.of(18, 0), LocalTime.of(19, 0), true));
        slots.save(new TimeSlot(LocalTime.of(19, 0), LocalTime.of(20, 0), true));
        slots.save(new TimeSlot(LocalTime.of(20, 0), LocalTime.of(21, 0), true));
        Venue turf = venues.save(new Venue("Green Turf Ground", "North Campus", football, "OUTDOOR", new BigDecimal("1200")));
        venues.save(new Venue("Futsal Arena", "Sports Complex Block A", football, "INDOOR", new BigDecimal("1500")));
        venues.save(new Venue("Hoops Indoor Court", "Sports Complex Block A", basketball, "INDOOR", new BigDecimal("800")));
        venues.save(new Venue("Street Court", "East Gate", basketball, "OUTDOOR", new BigDecimal("400")));
        venues.save(new Venue("Clay Court 1", "Tennis Club", tennis, "OUTDOOR", new BigDecimal("600")));
        venues.save(new Venue("Shuttle Hall", "Sports Complex Block B", badminton, "INDOOR", new BigDecimal("350")));
        venues.save(new Venue("Cricket Nets", "South Ground", cricket, "OUTDOOR", new BigDecimal("500")));
        bookings.save(new Booking(player, turf, morning, LocalDate.now().plusDays(1))); // a PENDING booking for the admin to approve
    }
}
