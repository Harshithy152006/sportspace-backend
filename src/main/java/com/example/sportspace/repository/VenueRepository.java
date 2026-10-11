package com.example.sportspace.repository;
import com.example.sportspace.entity.Venue; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface VenueRepository extends JpaRepository<Venue, Long> { List<Venue> findByArchivedFalse(); boolean existsBySportId(Long sportId); }
