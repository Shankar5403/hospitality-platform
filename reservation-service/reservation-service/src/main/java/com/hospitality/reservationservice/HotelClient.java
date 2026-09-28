package com.hospitality.reservationservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "hotel-service")
public interface HotelClient {

    @GetMapping("/hotels/{id}")
    HotelDto getHotel(@PathVariable Long id);

    @PostMapping("/hotels/{id}/reserve")
    HotelDto reserveRoom(@PathVariable Long id);

    @PostMapping("/hotels/{id}/release")
    HotelDto releaseRoom(@PathVariable Long id);
}