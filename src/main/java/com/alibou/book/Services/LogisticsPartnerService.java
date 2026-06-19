package com.alibou.book.Services;

import com.alibou.book.Entity.LogisticsPartner;
import com.alibou.book.Repositories.LogisticsPartnerRepository;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogisticsPartnerService {

    private final LogisticsPartnerRepository logisticsPartnerRepository;
    private final LogisticsPortalService logisticsPortalService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new RuntimeException("User not authenticated");
        return (User) auth.getPrincipal();
    }

    public List<LogisticsPartner> getAvailablePartners() {
        User user = getCurrentUser();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        if (isAdmin) {
            return logisticsPartnerRepository.findAll();
        } else {
            return logisticsPartnerRepository.findAvailablePartnersForUser(user.getId());
        }
    }

    /** Creates partner + auto-generates portal login account. Returns generated credentials. */
    public Map<String, String> addPartner(LogisticsPartner partner) {
        return logisticsPortalService.createPartnerAccount(partner);
    }

    public LogisticsPartner updatePartner(Long id, LogisticsPartner updatedPartner) {
        User user = getCurrentUser();
        LogisticsPartner partner = logisticsPartnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        if (!isAdmin && !partner.getCreatedBy().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized to update this partner");

        partner.setPartnerName(updatedPartner.getPartnerName());
        partner.setCompanyName(updatedPartner.getCompanyName());
        partner.setContactName(updatedPartner.getContactName());
        partner.setContactPhone(updatedPartner.getContactPhone());
        partner.setContactEmail(updatedPartner.getContactEmail());
        partner.setVehicleType(updatedPartner.getVehicleType());
        partner.setTrackAndTraceUrl(updatedPartner.getTrackAndTraceUrl());
        partner.setRegion(updatedPartner.getRegion());
        partner.setCapacityKg(updatedPartner.getCapacityKg());
        partner.setFlatRate(updatedPartner.getFlatRate());
        partner.setBaseRatePerKm(updatedPartner.getBaseRatePerKm());
        return logisticsPartnerRepository.save(partner);
    }

    public void deletePartner(Long id) {
        User user = getCurrentUser();
        LogisticsPartner partner = logisticsPartnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        if (!isAdmin && !partner.getCreatedBy().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized to delete this partner");
        logisticsPartnerRepository.delete(partner);
    }
}
