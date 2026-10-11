package com.example.sportspace.repository;
import com.example.sportspace.entity.TimeSlot; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> { List<TimeSlot> findAllByOrderByStartTimeAsc(); List<TimeSlot> findByActiveTrueOrderByStartTimeAsc(); }
