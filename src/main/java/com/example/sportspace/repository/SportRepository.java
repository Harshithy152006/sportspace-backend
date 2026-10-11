package com.example.sportspace.repository;
import com.example.sportspace.entity.Sport; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface SportRepository extends JpaRepository<Sport, Long> { Optional<Sport> findByNameIgnoreCase(String name); List<Sport> findAllByOrderByNameAsc(); }
