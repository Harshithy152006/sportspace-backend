package com.example.sportspace.controller;
import com.example.sportspace.entity.TimeSlot; import com.example.sportspace.service.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.LocalTime; import java.util.List;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/slots")
public class TimeSlotController {
    public record SlotRequest(@NotNull(message = "Start time is required") LocalTime startTime, @NotNull(message = "End time is required") LocalTime endTime, Boolean active) {}
    private final TimeSlotService service; private final UserService users;
    public TimeSlotController(TimeSlotService s, UserService u) { service = s; users = u; }
    @GetMapping public List<TimeSlot> all() { return service.all(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TimeSlot add(@RequestHeader(value = "X-User-Id", required = false) Long uid, @Valid @RequestBody SlotRequest r) { users.requireAdmin(uid); return service.add(r.startTime(), r.endTime(), r.active() == null || r.active()); }
    @PutMapping("/{id}")
    public TimeSlot update(@RequestHeader(value = "X-User-Id", required = false) Long uid, @PathVariable Long id, @Valid @RequestBody SlotRequest r) { users.requireAdmin(uid); return service.update(id, r.startTime(), r.endTime(), r.active() == null || r.active()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = "X-User-Id", required = false) Long uid, @PathVariable Long id) { users.requireAdmin(uid); service.delete(id); }
}
