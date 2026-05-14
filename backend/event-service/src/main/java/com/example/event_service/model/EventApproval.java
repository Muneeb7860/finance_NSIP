package com.example.event_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable audit trail for each approval/rejection action.
 * Every time an admin acts on an event, a record is created here.
 * This gives full traceability: who approved/rejected what, when, and why.
 */
@Entity
@Table(name = "event_approvals")
@Data
public class EventApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID eventId;

    /** The ADMIN user who took this action. */
    @Column(nullable = false)
    private UUID approverUserId;

    /** Name of the approver (denormalized for audit readability). */
    private String approverName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalAction action;

    /** Optional comment from the reviewer (required on rejection). */
    private String comment;

    private LocalDateTime actionTimestamp = LocalDateTime.now();

    /** 
     * Cryptographic fields for Digital Trust (Blockchain-lite).
     * Every record is linked to the previous one via a hash chain.
     */
    @Column(length = 64)
    private String previousHash;

    @Column(length = 64, unique = true)
    private String currentHash;

    public enum ApprovalLevel {
        L1_REVIEWER,
        L2_MANAGER,
        L3_DIRECTOR
    }

    public enum ApprovalAction {
        APPROVED,
        REJECTED,
        SENT_BACK  // Sent back to previous level for revision
    }
}
