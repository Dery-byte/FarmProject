package com.alibou.book.Controllers;

import com.alibou.book.Entity.FarmInventory;
import com.alibou.book.Entity.HealthAndMaintenance;
import com.alibou.book.Entity.ResourceUsage;
import com.alibou.book.Services.FarmManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/farm-management")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Farm Management Lite")
public class FarmManagementController {

    private final FarmManagementService service;

    // Inventory Endpoints
    @GetMapping("/{farmId}/inventory")
    public ResponseEntity<List<FarmInventory>> getInventory(@PathVariable Long farmId) {
        return ResponseEntity.ok(service.getInventoryByFarm(farmId));
    }

    @PostMapping("/{farmId}/inventory")
    public ResponseEntity<FarmInventory> addInventory(@PathVariable Long farmId, @RequestBody FarmInventory item) {
        return ResponseEntity.ok(service.addInventoryItem(farmId, item));
    }

    @DeleteMapping("/{farmId}/inventory/{itemId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long farmId, @PathVariable Long itemId) {
        service.deleteInventoryItem(farmId, itemId);
        return ResponseEntity.ok().build();
    }

    // Resource Usage Endpoints
    @GetMapping("/{farmId}/resource-usage")
    public ResponseEntity<List<ResourceUsage>> getResourceUsage(@PathVariable Long farmId) {
        return ResponseEntity.ok(service.getResourceUsageByFarm(farmId));
    }

    @PostMapping("/{farmId}/resource-usage")
    public ResponseEntity<ResourceUsage> addResourceUsage(@PathVariable Long farmId, @RequestBody ResourceUsage usage) {
        return ResponseEntity.ok(service.addResourceUsage(farmId, usage));
    }

    @DeleteMapping("/{farmId}/resource-usage/{usageId}")
    public ResponseEntity<Void> deleteResourceUsage(@PathVariable Long farmId, @PathVariable Long usageId) {
        service.deleteResourceUsage(farmId, usageId);
        return ResponseEntity.ok().build();
    }

    // Health and Maintenance Endpoints
    @GetMapping("/{farmId}/health-maintenance")
    public ResponseEntity<List<HealthAndMaintenance>> getHealthAndMaintenance(@PathVariable Long farmId) {
        return ResponseEntity.ok(service.getHealthAndMaintenanceByFarm(farmId));
    }

    @PostMapping("/{farmId}/health-maintenance")
    public ResponseEntity<HealthAndMaintenance> addHealthAndMaintenance(@PathVariable Long farmId, @RequestBody HealthAndMaintenance hm) {
        return ResponseEntity.ok(service.addHealthAndMaintenance(farmId, hm));
    }

    @DeleteMapping("/{farmId}/health-maintenance/{hmId}")
    public ResponseEntity<Void> deleteHealthAndMaintenance(@PathVariable Long farmId, @PathVariable Long hmId) {
        service.deleteHealthAndMaintenance(farmId, hmId);
        return ResponseEntity.ok().build();
    }
}
