package com.alibou.book.Services;

import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.FarmInventory;
import com.alibou.book.Entity.HealthAndMaintenance;
import com.alibou.book.Entity.ResourceUsage;
import com.alibou.book.Repositories.FarmInventoryRepository;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.Repositories.HealthAndMaintenanceRepository;
import com.alibou.book.Repositories.ResourceUsageRepository;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmManagementService {

    private final FarmRepository farmRepository;
    private final FarmInventoryRepository farmInventoryRepository;
    private final ResourceUsageRepository resourceUsageRepository;
    private final HealthAndMaintenanceRepository healthAndMaintenanceRepository;

    private void validateFarmerAccess(Long farmId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        User user = (User) auth.getPrincipal();

        if (!user.isFarmManagementLiteEnabled()) {
            throw new RuntimeException("Farm Management Lite is not enabled for this user");
        }

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        if (!farm.getFarmer().getId().equals(user.getId())) {
            throw new RuntimeException("User does not own this farm");
        }
    }

    private Farm getFarm(Long farmId) {
        return farmRepository.findById(farmId).orElseThrow(() -> new RuntimeException("Farm not found"));
    }

    // Inventory
    public List<FarmInventory> getInventoryByFarm(Long farmId) {
        validateFarmerAccess(farmId);
        return farmInventoryRepository.findByFarmId(farmId);
    }

    public FarmInventory addInventoryItem(Long farmId, FarmInventory item) {
        validateFarmerAccess(farmId);
        item.setFarm(getFarm(farmId));
        return farmInventoryRepository.save(item);
    }

    public void deleteInventoryItem(Long farmId, Long itemId) {
        validateFarmerAccess(farmId);
        farmInventoryRepository.deleteById(itemId);
    }

    // Resource Usage
    public List<ResourceUsage> getResourceUsageByFarm(Long farmId) {
        validateFarmerAccess(farmId);
        return resourceUsageRepository.findByFarmId(farmId);
    }

    public ResourceUsage addResourceUsage(Long farmId, ResourceUsage usage) {
        validateFarmerAccess(farmId);
        usage.setFarm(getFarm(farmId));
        return resourceUsageRepository.save(usage);
    }

    public void deleteResourceUsage(Long farmId, Long usageId) {
        validateFarmerAccess(farmId);
        resourceUsageRepository.deleteById(usageId);
    }

    // Health And Maintenance
    public List<HealthAndMaintenance> getHealthAndMaintenanceByFarm(Long farmId) {
        validateFarmerAccess(farmId);
        return healthAndMaintenanceRepository.findByFarmId(farmId);
    }

    public HealthAndMaintenance addHealthAndMaintenance(Long farmId, HealthAndMaintenance hm) {
        validateFarmerAccess(farmId);
        hm.setFarm(getFarm(farmId));
        return healthAndMaintenanceRepository.save(hm);
    }

    public void deleteHealthAndMaintenance(Long farmId, Long hmId) {
        validateFarmerAccess(farmId);
        healthAndMaintenanceRepository.deleteById(hmId);
    }
}
