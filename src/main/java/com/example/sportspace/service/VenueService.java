package com.example.sportspace.service;
import com.example.sportspace.entity.Venue; import com.example.sportspace.exception.MyException; import com.example.sportspace.repository.VenueRepository;
import java.util.List; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service;
@Service
public class VenueService {
    private final VenueRepository repo; public VenueService(VenueRepository repo) { this.repo = repo; }
    public List<Venue> all() { return repo.findByArchivedFalse(); }
    public Venue find(Long id) { return repo.findById(id).filter(v -> !v.isArchived()).orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Venue not found")); }
    public Venue add(Venue v) { return repo.save(v); }
    public Venue update(Long id, Venue in) {
        Venue v = find(id); v.setName(in.getName()); v.setLocation(in.getLocation()); v.setSport(in.getSport());
        v.setVenueType(in.getVenueType()); v.setPricePerSlot(in.getPricePerSlot()); return repo.save(v);
    }
    public void archive(Long id) { Venue v = find(id); v.setArchived(true); repo.save(v); }
}
