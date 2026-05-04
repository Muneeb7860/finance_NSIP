package com.example.contribution_service.repository;

import com.example.contribution_service.model.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

public interface ContributionRepository extends JpaRepository<Contribution, UUID> {

    /**
     * Calculate the total savings for a user across all their contributions.
     */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Contribution c WHERE c.employment.userId = :userId AND c.status = 'Paid'")
    BigDecimal getTotalSavingsByUserId(UUID userId);

    /**
     * Find the earliest contribution date for a user (used for vesting validation).
     */
    @Query("SELECT MIN(c.createdAt) FROM Contribution c WHERE c.employment.userId = :userId")
    java.util.Optional<java.time.LocalDateTime> getFirstContributionDate(UUID userId);
}
