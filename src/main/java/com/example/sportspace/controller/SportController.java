package com.example.sportspace.controller;
import com.example.sportspace.entity.Sport; import com.example.sportspace.service.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.util.List;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/sports")
public class SportController {
    public record SportRequest(@NotBlank(message = "Sport name is required") String name) {}
    private final SportService service; private final UserService users;
    public SportController(SportService s, UserService u) { service = s; users = u; }
    @GetMapping public List<Sport> all() { return service.all(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Sport add(@RequestHeader(value = "X-User-Id", required = false) Long uid, @Valid @RequestBody SportRequest r) { users.requireAdmin(uid); return service.add(r.name()); }
    @PutMapping("/{id}")
    public Sport update(@RequestHeader(value = "X-User-Id", required = false) Long uid, @PathVariable Long id, @Valid @RequestBody SportRequest r) { users.requireAdmin(uid); return service.update(id, r.name()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = "X-User-Id", required = false) Long uid, @PathVariable Long id) { users.requireAdmin(uid); service.delete(id); }
}
