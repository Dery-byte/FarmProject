package com.alibou.book.Repositories;

import com.alibou.book.Entity.LogisticsPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogisticsPartnerRepository extends JpaRepository<LogisticsPartner, Long> {

    // Find all global partners
    List<LogisticsPartner> findByIsGlobalTrue();

    // Find all global partners OR partners created by a specific user
    @Query("SELECT l FROM LogisticsPartner l WHERE l.isGlobal = true OR l.createdBy.id = :userId")
    List<LogisticsPartner> findAvailablePartnersForUser(@Param("userId") Integer userId);
    
    // Find partners strictly created by a specific user (for management dashboard)
    List<LogisticsPartner> findByCreatedById(Integer userId);

    // Find a partner by their portal user account
    @Query("SELECT l FROM LogisticsPartner l WHERE l.portalUser.id = :userId")
    Optional<LogisticsPartner> findByPortalUserId(@Param("userId") Integer userId);
}
