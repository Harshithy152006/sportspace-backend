package com.example.sportspace.controller;
import com.example.sportspace.entity.User; import com.example.sportspace.service.UserService;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/users")
public class UserController {
    public record RegisterRequest(@NotBlank(message = "Name is required") String name, @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email,
                                  @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password) {}
    public record LoginRequest(@NotBlank(message = "Email is required") String email, @NotBlank(message = "Password is required") String password) {}
    private final UserService service; public UserController(UserService s) { service = s; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public User register(@Valid @RequestBody RegisterRequest r) { return service.register(r.name(), r.email(), r.password()); }
    @PostMapping("/login") public User login(@Valid @RequestBody LoginRequest r) { return service.login(r.email(), r.password()); }
}
