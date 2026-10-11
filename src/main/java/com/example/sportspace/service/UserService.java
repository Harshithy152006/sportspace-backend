package com.example.sportspace.service;
import com.example.sportspace.entity.User; import com.example.sportspace.exception.MyException; import com.example.sportspace.repository.UserRepository;
import org.springframework.http.HttpStatus; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.stereotype.Service;
@Service
public class UserService {
    private final UserRepository repo; private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    public UserService(UserRepository repo) { this.repo = repo; }
    public User register(String name, String email, String password) {
        if (repo.findByEmail(email).isPresent()) throw new MyException(HttpStatus.CONFLICT, "Email already registered");
        return repo.save(new User(name, email, enc.encode(password), "USER"));
    }
    public User login(String email, String password) {
        return repo.findByEmail(email).filter(u -> enc.matches(password, u.getPasswordHash()))
            .orElseThrow(() -> new MyException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }
    public User requireUser(Long id) {
        if (id == null) throw new MyException(HttpStatus.UNAUTHORIZED, "Please log in");
        return repo.findById(id).orElseThrow(() -> new MyException(HttpStatus.UNAUTHORIZED, "Please log in"));
    }
    public User requireAdmin(Long id) { User u = requireUser(id); if (!"ADMIN".equals(u.getRole())) throw new MyException(HttpStatus.FORBIDDEN, "Admin only"); return u; }
}
