package com.example.sportspace.service;
import com.example.sportspace.entity.TimeSlot; import com.example.sportspace.exception.MyException;
import com.example.sportspace.repository.BookingRepository; import com.example.sportspace.repository.TimeSlotRepository;
import java.time.LocalTime; import java.util.List; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service;
@Service
public class TimeSlotService {
    private final TimeSlotRepository repo; private final BookingRepository bookings;
    public TimeSlotService(TimeSlotRepository repo, BookingRepository bookings) { this.repo = repo; this.bookings = bookings; }
    public List<TimeSlot> all() { return repo.findAllByOrderByStartTimeAsc(); }
    public List<TimeSlot> active() { return repo.findByActiveTrueOrderByStartTimeAsc(); }
    public TimeSlot find(Long id) { return repo.findById(id).orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Time slot not found")); }
    public TimeSlot add(LocalTime start, LocalTime end, boolean active) { check(null, start, end); return repo.save(new TimeSlot(start, end, active)); }
    public TimeSlot update(Long id, LocalTime start, LocalTime end, boolean active) {
        TimeSlot s = find(id); check(id, start, end);
        s.setStartTime(start); s.setEndTime(end); s.setActive(active); return repo.save(s);
    }
    public void delete(Long id) {
        TimeSlot s = find(id);
        if (bookings.existsBySlotId(id)) throw new MyException(HttpStatus.CONFLICT, "Bookings use this slot. Mark it inactive instead of deleting");
        repo.delete(s);
    }
    // End must be after start, and two slots must not overlap (a < d and c < b means [a,b) and [c,d) overlap).
    private void check(Long selfId, LocalTime start, LocalTime end) {
        if (!end.isAfter(start)) throw new MyException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        for (TimeSlot o : repo.findAll())
            if (!o.getId().equals(selfId) && start.isBefore(o.getEndTime()) && o.getStartTime().isBefore(end)) throw new MyException(HttpStatus.CONFLICT, "This slot overlaps an existing slot");
    }
}
