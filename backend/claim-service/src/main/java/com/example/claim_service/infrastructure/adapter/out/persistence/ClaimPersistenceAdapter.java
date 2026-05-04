package com.example.claim_service.infrastructure.adapter.out.persistence;

import com.example.claim_service.application.port.out.ClaimRepositoryPort;
import com.example.claim_service.domain.model.Claim;
import com.example.claim_service.repository.ClaimRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class ClaimPersistenceAdapter implements ClaimRepositoryPort {

    private final ClaimRepository jpaRepository;

    public ClaimPersistenceAdapter(ClaimRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Claim save(Claim claim) {
        Objects.requireNonNull(claim, "Claim cannot be null");
        com.example.claim_service.model.Claim jpaEntity = Objects.requireNonNull(mapToJpa(claim));
        com.example.claim_service.model.Claim savedEntity = jpaRepository.save(jpaEntity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Claim> findById(String id) {
        Objects.requireNonNull(id, "ID cannot be null");
        UUID uuid = Objects.requireNonNull(UUID.fromString(id));
        return jpaRepository.findById(uuid).map(this::mapToDomain);
    }

    @Override
    public List<Claim> findByUserId(String userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        UUID uuid = Objects.requireNonNull(UUID.fromString(userId));
        return jpaRepository.findByUserId(uuid).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private com.example.claim_service.model.Claim mapToJpa(Claim domain) {
        com.example.claim_service.model.Claim jpa = new com.example.claim_service.model.Claim();
        if (domain.getId() != null) jpa.setId(UUID.fromString(domain.getId()));
        jpa.setUserId(UUID.fromString(domain.getUserId()));
        jpa.setAmount(domain.getAmount());
        jpa.setDescription(domain.getDescription());
        // Map types and status (simplifying for demo)
        jpa.setClaimType(com.example.claim_service.model.Claim.ClaimType.valueOf(domain.getType().name()));
        jpa.setStatus(com.example.claim_service.model.Claim.ClaimStatus.valueOf(domain.getStatus().name()));
        return jpa;
    }

    private Claim mapToDomain(com.example.claim_service.model.Claim jpa) {
        Claim domain = new Claim();
        domain.setId(jpa.getId().toString());
        domain.setUserId(jpa.getUserId().toString());
        domain.setAmount(jpa.getAmount());
        domain.setDescription(jpa.getDescription());
        domain.setCreatedAt(jpa.getCreatedAt());
        // Map types and status
        domain.setType(Claim.ClaimType.valueOf(jpa.getClaimType().name()));
        domain.setStatus(Claim.ClaimStatus.valueOf(jpa.getStatus().name()));
        return domain;
    }
}
