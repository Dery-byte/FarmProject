package com.alibou.book.Controllers;

import com.alibou.book.Entity.LogisticsPartner;
import com.alibou.book.Services.LogisticsPartnerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Logistics Partners")
public class LogisticsPartnerController {

    private final LogisticsPartnerService service;

    @GetMapping
    public ResponseEntity<List<LogisticsPartner>> getAvailablePartners() {
        return ResponseEntity.ok(service.getAvailablePartners());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addPartner(@RequestBody LogisticsPartner partner) {
        return ResponseEntity.ok(service.addPartner(partner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LogisticsPartner> updatePartner(@PathVariable Long id, @RequestBody LogisticsPartner partner) {
        return ResponseEntity.ok(service.updatePartner(id, partner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        service.deletePartner(id);
        return ResponseEntity.ok().build();
    }
}
