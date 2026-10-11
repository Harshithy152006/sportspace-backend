package com.example.sportspace.service;
import com.example.sportspace.entity.*; import com.example.sportspace.exception.MyException; import com.example.sportspace.repository.BookingRepository;
import java.time.*; import java.util.List; import java.util.Locale; import java.util.regex.Pattern;
import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service;
@Service
public class BookingService {
    public record SlotAvailability(Long slotId, LocalTime startTime, LocalTime endTime, boolean available) {}
    private static final Pattern UPI_REFERENCE = Pattern.compile("[A-Za-z0-9]{8,30}");
    private final BookingRepository repo; private final VenueService venueService; private final TimeSlotService slotService;
    public BookingService(BookingRepository repo, VenueService venueService, TimeSlotService slotService) { this.repo = repo; this.venueService = venueService; this.slotService = slotService; }
    public List<SlotAvailability> availability(Long venueId, LocalDate date) {
        Venue v = venueService.find(venueId); List<Long> taken = repo.takenSlotIds(v.getId(), date, LocalDateTime.now());
        return slotService.active().stream().map(s -> new SlotAvailability(s.getId(), s.getStartTime(), s.getEndTime(), !taken.contains(s.getId()) && !started(date, s))).toList();
    }
    public synchronized Booking create(User user, Long venueId, Long slotId, LocalDate date, String method) {
        String pm = method == null ? "" : method.trim().toUpperCase(Locale.ROOT);
        if (!List.of("UPI", "CASH").contains(pm)) throw new MyException(HttpStatus.BAD_REQUEST, "Choose UPI or cash at venue");
        Venue v = venueService.find(venueId); TimeSlot s = slotService.find(slotId);
        if (!s.isActive()) throw new MyException(HttpStatus.CONFLICT, "This time slot is not open for booking");
        if (started(date, s)) throw new MyException(HttpStatus.BAD_REQUEST, "This slot has already started");
        if (repo.countTaken(v.getId(), s.getId(), date, LocalDateTime.now()) > 0) throw new MyException(HttpStatus.CONFLICT, "This venue is already booked for that date and slot");
        Booking b = new Booking(user, v, s, date); b.setPaymentMethod(pm); b.setPaymentStatus("PENDING");
        if ("UPI".equals(pm)) b.setPaymentExpiresAt(LocalDateTime.now().plusMinutes(15));
        return repo.save(b);
    }
    public Booking submitReference(User user, Long id, String reference) {
        Booking b = find(id); ownerOrAdmin(user,b);
        if (!"UPI".equals(b.getPaymentMethod())) throw new MyException(HttpStatus.CONFLICT,"Only UPI bookings accept a transaction reference");
        if ("PAID".equals(b.getPaymentStatus())) throw new MyException(HttpStatus.CONFLICT,"This payment is already verified");
        if ("CANCELLED".equals(b.getStatus()) || "REJECTED".equals(b.getStatus())) throw new MyException(HttpStatus.CONFLICT,"This booking is no longer active");
        String ref = reference == null ? "" : reference.trim();
        if (!UPI_REFERENCE.matcher(ref).matches()) throw new MyException(HttpStatus.BAD_REQUEST,"Enter an 8–30 character transaction reference using letters and numbers only");
        boolean duplicate = repo.findAll().stream().anyMatch(other -> !other.getId().equals(b.getId()) && ref.equalsIgnoreCase(other.getPaymentReference()));
        if (duplicate) throw new MyException(HttpStatus.CONFLICT,"That transaction reference is already attached to another booking");
        b.setPaymentReference(ref); b.setPaymentStatus("PENDING_VERIFICATION"); b.setPaymentUpdatedAt(LocalDateTime.now()); b.setPaymentExpiresAt(null);
        return repo.save(b);
    }
    public Booking verify(User admin, Long id) { requireAdmin(admin); Booking b=find(id); if (!"UPI".equals(b.getPaymentMethod()) || !"PENDING_VERIFICATION".equals(b.getPaymentStatus())) throw new MyException(HttpStatus.CONFLICT,"Only UPI payments awaiting verification can be marked paid"); b.setPaymentStatus("PAID"); b.setPaymentUpdatedAt(LocalDateTime.now()); b.setPaymentExpiresAt(null); return repo.save(b); }
    public Booking rejectPayment(User admin, Long id) { requireAdmin(admin); Booking b=find(id); if (!"UPI".equals(b.getPaymentMethod()) || !"PENDING_VERIFICATION".equals(b.getPaymentStatus())) throw new MyException(HttpStatus.CONFLICT,"Only submitted UPI payments can be rejected"); b.setPaymentStatus("REJECTED"); b.setPaymentUpdatedAt(LocalDateTime.now()); b.setPaymentReference(null); b.setPaymentExpiresAt(LocalDateTime.now().plusMinutes(15)); return repo.save(b); }
    public Booking cashReceived(User admin, Long id) { requireAdmin(admin); Booking b=find(id); if (!"CASH".equals(b.getPaymentMethod()) || !"PENDING".equals(b.getPaymentStatus())) throw new MyException(HttpStatus.CONFLICT,"Only pending cash payments can be marked received"); if ("CANCELLED".equals(b.getStatus()) || "REJECTED".equals(b.getStatus())) throw new MyException(HttpStatus.CONFLICT,"This booking is no longer active"); b.setPaymentStatus("PAID"); b.setPaymentUpdatedAt(LocalDateTime.now()); return repo.save(b); }
    public List<Booking> mine(Long userId) { return repo.findByUserIdOrderByIdDesc(userId); }
    public List<Booking> all() { return repo.findAllByOrderByIdDesc(); }
    public synchronized Booking change(User user, Long id, String action) {
        Booking b = find(id);
        switch (action) {
            case "cancel" -> { ownerOrAdmin(user,b); if (b.getBookingDate().isBefore(LocalDate.now())) throw new MyException(HttpStatus.CONFLICT, "Past bookings cannot be cancelled"); move(b, List.of("PENDING", "APPROVED"), "CANCELLED"); }
            case "approve" -> { requireAdmin(user); move(b, List.of("PENDING"), "APPROVED"); }
            case "reject" -> { requireAdmin(user); move(b, List.of("PENDING"), "REJECTED"); }
            default -> throw new MyException(HttpStatus.BAD_REQUEST, "Unknown action");
        }
        return repo.save(b);
    }
    private Booking find(Long id) { return repo.findById(id).orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND,"Booking not found")); }
    private void ownerOrAdmin(User u, Booking b) { if (!"ADMIN".equals(u.getRole()) && !b.getUser().getId().equals(u.getId())) throw new MyException(HttpStatus.FORBIDDEN,"Not your booking"); }
    private boolean started(LocalDate date, TimeSlot s) { return date.isBefore(LocalDate.now()) || (date.equals(LocalDate.now()) && !s.getStartTime().isAfter(LocalTime.now())); }
    private void requireAdmin(User u) { if (!"ADMIN".equals(u.getRole())) throw new MyException(HttpStatus.FORBIDDEN, "Admin only"); }
    private void move(Booking b, List<String> from, String to) { if (!from.contains(b.getStatus())) throw new MyException(HttpStatus.CONFLICT, "Cannot change a " + b.getStatus() + " booking to " + to); b.setStatus(to); }
}
