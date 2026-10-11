package com.example.sportspace.service;
import com.example.sportspace.entity.Sport; import com.example.sportspace.exception.MyException;
import com.example.sportspace.repository.SportRepository; import com.example.sportspace.repository.VenueRepository;
import java.util.List; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service;
@Service
public class SportService {
    private final SportRepository repo; private final VenueRepository venues;
    public SportService(SportRepository repo, VenueRepository venues) { this.repo = repo; this.venues = venues; }
    public List<Sport> all() { return repo.findAllByOrderByNameAsc(); }
    public Sport find(Long id) { return repo.findById(id).orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Sport not found")); }
    public Sport add(String name) {
        if (repo.findByNameIgnoreCase(name.trim()).isPresent()) throw new MyException(HttpStatus.CONFLICT, "Sport already exists");
        return repo.save(new Sport(name.trim()));
    }
    public Sport update(Long id, String name) {
        Sport s = find(id);
        if (repo.findByNameIgnoreCase(name.trim()).filter(o -> !o.getId().equals(id)).isPresent()) throw new MyException(HttpStatus.CONFLICT, "Sport already exists");
        s.setName(name.trim()); return repo.save(s);
    }
    public void delete(Long id) {
        Sport s = find(id);
        if (venues.existsBySportId(id)) throw new MyException(HttpStatus.CONFLICT, "Venues still use this sport, so it cannot be deleted");
        repo.delete(s);
    }
}
