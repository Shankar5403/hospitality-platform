package com.hospitality.reservationservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository repo;

    @Autowired
    private HotelClient hotelClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping
    public Reservation book(@RequestBody Reservation req) {
        // 1. Get hotel info from hotel-service via Feign
        HotelDto hotel = hotelClient.getHotel(req.getHotelId());

        // 2. Check availability
        if (hotel.getAvailableRooms() <= 0) {
            req.setStatus("FAILED");
            return repo.save(req);
        }

        // 3. Compute price (dynamic: +10% if less than 5 rooms left)
        double price = hotel.getPricePerNight() * req.getNights();
        if (hotel.getAvailableRooms() < 5) {
            price = price * 1.10;
        }
        req.setTotalPrice(price);

        // 4. Reserve room in hotel-service
        hotelClient.reserveRoom(req.getHotelId());
        req.setStatus("CONFIRMED");
        Reservation saved = repo.save(req);

        // 5. Publish booking event to RabbitMQ
        String msg = saved.getId() + "," + saved.getCustomerEmail()
                + "," + saved.getCustomerName() + "," + saved.getTotalPrice();
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, msg);

        return saved;
    }

    @GetMapping
    public List<Reservation> all() {
        return repo.findAll();
    }

    @DeleteMapping("/{id}")
    public Reservation cancel(@PathVariable Long id) {
        Reservation r = repo.findById(id).orElseThrow();
        if ("CONFIRMED".equals(r.getStatus())) {
            hotelClient.releaseRoom(r.getHotelId());
            r.setStatus("CANCELLED");
        }
        return repo.save(r);
    }
}