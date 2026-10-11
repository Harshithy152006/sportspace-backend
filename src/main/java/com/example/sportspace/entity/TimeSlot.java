package com.example.sportspace.entity;
import jakarta.persistence.*; import java.time.LocalTime;
@Entity
public class TimeSlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private LocalTime startTime; private LocalTime endTime; private boolean active = true;
    public TimeSlot() {}
    public TimeSlot(LocalTime s, LocalTime e, boolean a) { startTime = s; endTime = e; active = a; }
    public Long getId() { return id; } public LocalTime getStartTime() { return startTime; } public LocalTime getEndTime() { return endTime; } public boolean isActive() { return active; }
    public void setStartTime(LocalTime s) { startTime = s; } public void setEndTime(LocalTime e) { endTime = e; } public void setActive(boolean a) { active = a; }
}
