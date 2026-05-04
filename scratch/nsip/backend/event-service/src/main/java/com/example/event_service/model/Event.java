package com.example.event_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.DRAFT;

    /** The EMPLOYER user who submitted this event proposal. */
    @Column(nullable = false)
    private UUID createdByUserId;

    /** The organization/company name of the business owner. */
    private String organizationName;

    private Integer attendancePointsReward;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String mapLocation; // Latitude/Longitude or URL
    private String speakers; // Comma separated or JSON
    private String features; // e.g. "WiFi, Catering, Recording"
    private Integer maxCapacity;
    private LocalDateTime createdAt = LocalDateTime.now();

    // ---- Enums ----

    public enum EventType {
        DIGITAL, PHYSICAL
    }

    /**
     * Business-driven event categories.
     * These represent the types of events employers can propose.
     */
    public enum EventCategory {
        RAMADAN_EVENT,
        CHARITY_RUN,
        ANNIVERSARY_MEETUP,
        SUCCESS_MEETUP,
        FINANCIAL_LITERACY,
        WELLNESS,
        TEAM_BUILDING,
        OTHER
    }

    /**
     * 3-layer approval pipeline.
     *
     *  DRAFT → L1_APPROVED → L2_APPROVED → LIVE
     *    ↓          ↓             ↓
     *  REJECTED  REJECTED     REJECTED
     */
    public enum ApprovalStatus {
        DRAFT,          // Submitted by employer, awaiting L1 review
        L1_APPROVED,    // Passed L1 Reviewer, awaiting L2 Manager
        L2_APPROVED,    // Passed L2 Manager, awaiting L3 Director
        LIVE,           // Passed all 3 layers — visible to contributors
        REJECTED        // Rejected at any layer
    }
}
