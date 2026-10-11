package com.example.sportspace.repository;
import com.example.sportspace.entity.Booking; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByIdDesc(Long userId);
    List<Booking> findAllByOrderByIdDesc();
    boolean existsBySlotId(Long slotId);
    @Query("select count(b) from Booking b where b.venue.id = :vid and b.slot.id = :sid and b.bookingDate = :d and b.status in ('PENDING','APPROVED') and (b.paymentMethod is null or b.paymentMethod = 'CASH' or b.paymentStatus = 'PAID' or b.paymentStatus = 'PENDING_VERIFICATION' or b.paymentExpiresAt > :now)")
    long countTaken(@Param("vid") Long venueId, @Param("sid") Long slotId, @Param("d") LocalDate date, @Param("now") LocalDateTime now);
    @Query("select b.slot.id from Booking b where b.venue.id = :vid and b.bookingDate = :d and b.status in ('PENDING','APPROVED') and (b.paymentMethod is null or b.paymentMethod = 'CASH' or b.paymentStatus = 'PAID' or b.paymentStatus = 'PENDING_VERIFICATION' or b.paymentExpiresAt > :now)")
    List<Long> takenSlotIds(@Param("vid") Long venueId, @Param("d") LocalDate date, @Param("now") LocalDateTime now);
}
