package com.example.sportspace.exception;
import java.util.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MyException.class)
    public ResponseEntity<Map<String, Object>> mine(MyException e) { return ResponseEntity.status(e.getStatus()).body(Map.of("message", e.getMessage())); }
    // Validation errors (@Valid): 400 with a field -> message map.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        Map<String, String> fields = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> fields.put(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("message", String.join(", ", fields.values()), "fields", fields));
    }
    // Bad JSON, wrong date/time format, wrong parameter type: 400.
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> badInput(Exception e) { return ResponseEntity.badRequest().body(Map.of("message", "Invalid request data (dates must be yyyy-MM-dd, times HH:mm)")); }
    // Database constraint hit (for example deleting something still in use): 409.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> conflict(DataIntegrityViolationException e) { log.warn("Data integrity violation", e); return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "This change conflicts with existing data")); }
    // Anything unexpected: logged on the server, generic message to the client.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> unexpected(RuntimeException e) { log.error("Unexpected error", e); return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong on the server")); }
}
