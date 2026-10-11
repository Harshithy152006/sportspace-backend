package com.example.sportspace.entity;
import jakarta.persistence.*; import java.math.BigDecimal;
@Entity
public class Venue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name; private String location; @ManyToOne(optional = false) private Sport sport;
    private String venueType; // INDOOR or OUTDOOR
    private BigDecimal pricePerSlot; private boolean archived = false;
    public Venue() {}
    public Venue(String n, String l, Sport s, String t, BigDecimal p) { name = n; location = l; sport = s; venueType = t; pricePerSlot = p; }
    public Long getId() { return id; } public String getName() { return name; } public String getLocation() { return location; } public Sport getSport() { return sport; }
    public String getVenueType() { return venueType; } public BigDecimal getPricePerSlot() { return pricePerSlot; } public boolean isArchived() { return archived; }
    public void setName(String n) { name = n; } public void setLocation(String l) { location = l; } public void setSport(Sport s) { sport = s; }
    public void setVenueType(String t) { venueType = t; } public void setPricePerSlot(BigDecimal p) { pricePerSlot = p; } public void setArchived(boolean a) { archived = a; }
}
