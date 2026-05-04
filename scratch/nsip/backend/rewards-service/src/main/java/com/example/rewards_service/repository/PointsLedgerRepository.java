package com.example.rewards_service.repository;

import com.example.rewards_service.model.PointsLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface PointsLedgerRepository extends JpaRepository<PointsLedger, UUID> {
    List<PointsLedger> findByUserId(UUID userId);

    @Query("SELECT COALESCE(SUM(p.pointDelta), 0) FROM PointsLedger p WHERE p.userId = :userId")
    int getTotalPointsByUserId(UUID userId);
}
