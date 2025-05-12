//package com.alibou.book.Controllers;
//
//import com.alibou.book.Entity.Delivery;
//import com.alibou.book.Services.DeliveryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/deliveries")
//@RequiredArgsConstructor
//public class DeliveryController {
//    private final DeliveryService deliveryService;
//
////    @PostMapping("/create")
////    public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery) {
////        return ResponseEntity.ok(deliveryService.createDelivery(delivery));
////    }
//
//    @GetMapping
//    public ResponseEntity<List<Delivery>> getAllDeliveries() {
//        return ResponseEntity.ok(deliveryService.getAllDeliveries());
//    }
//}
