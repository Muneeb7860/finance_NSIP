package com.example.contribution_service.repository;

import com.example.contribution_service.model.FundLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface FundLockRepository extends JpaRepository<FundLock, UUID> {
    Optional<FundLock> findByClaimId(UUID claimId);

    /**
     * Total currently locked amount for a user (prevents over-locking).
     */
    @Query("SELECT COALESCE(SUM(f.lockedAmount), 0) FROM FundLock f WHERE f.userId = :userId AND f.status = 'LOCKED'")
    BigDecimal getTotalLockedByUserId(UUID userId);
}
