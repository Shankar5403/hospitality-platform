package com.hospitality.hotelservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelRepository repo;

    @PostConstruct
    public void seed() {
        Hotel h1 = new Hotel();
        h1.setName("Taj Palace");
        h1.setCity("Mumbai");
        h1.setTotalRooms(50);
        h1.setAvailableRooms(50);
        h1.setPricePerNight(5000);
        repo.save(h1);

        Hotel h2 = new Hotel();
        h2.setName("Oberoi");
        h2.setCity("Delhi");
        h2.setTotalRooms(30);
        h2.setAvailableRooms(30);
        h2.setPricePerNight(7000);
        repo.save(h2);
    }

    @GetMapping
    public List<Hotel> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Hotel getOne(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public Hotel create(@RequestBody Hotel h) {
        return repo.save(h);
    }

    @PostMapping("/{id}/reserve")
    public Hotel reserve(@PathVariable Long id) {
        Hotel h = repo.findById(id).orElseThrow();
        if (h.getAvailableRooms() <= 0) {
            throw new RuntimeException("No rooms available");
        }
        h.setAvailableRooms(h.getAvailableRooms() - 1);
        return repo.save(h);
    }
}