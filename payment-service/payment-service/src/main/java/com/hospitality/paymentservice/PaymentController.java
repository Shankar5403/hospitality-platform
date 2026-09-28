package com.hospitality.paymentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository repo;

    @PostMapping
    public Payment process(@RequestBody Payment req) {
        // Simulation: if cardNumber ends with even digit → FAILED, else SUCCESS
        char last = req.getCardNumber().charAt(req.getCardNumber().length() - 1);
        boolean success = (last - '0') % 2 != 0;

        if (success) {
            req.setStatus("SUCCESS");
            req.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            req.setStatus("FAILED");
            req.setTransactionId(null);
        }

        return repo.save(req);
    }

    @GetMapping
    public List<Payment> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Payment getOne(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }
}