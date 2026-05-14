package com.example.education_service.service;

import com.example.education_service.model.*;
import com.example.education_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@SuppressWarnings("null")
public class AdvisorService {

    @Autowired private AdvisorProfileRepository profileRepo;
    @Autowired private AdvisorSessionRepository sessionRepo;
    @Autowired private AdvisorReviewRepository reviewRepo;
    @Autowired private KafkaTemplate<String, String> kafkaTemplate;

    // =========================================================================
    // ADVISOR SELF-SERVICE: Register, manage profile, view schedule & ratings
    // =========================================================================

    /** Advisor lists themselves on the platform. */
    @Transactional
    public AdvisorProfile registerAdvisor(UUID userId, String name, String specialty, String bio) {
        if (profileRepo.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Advisor profile already exists.");
        }
        AdvisorProfile profile = new AdvisorProfile();
        profile.setUserId(userId);
        profile.setName(name);
        profile.setSpecialty(specialty);
        profile.setBio(bio);
        log.info("Advisor registered: {} ({})", name, specialty);
        return profileRepo.save(profile);
    }

    /** Advisor views their own schedule. */
    public List<AdvisorSession> getMySchedule(UUID advisorId) {
        return sessionRepo.findByAdvisorIdOrderByScheduledAtDesc(advisorId);
    }

    /** Advisor views their average rating and reviews. */
    public Map<String, Object> getMyRatings(UUID advisorId) {
        Objects.requireNonNull(advisorId, "Advisor ID cannot be null");
        AdvisorProfile profile = profileRepo.findById(advisorId)
                .orElseThrow(() -> new IllegalArgumentException("Advisor not found."));
        List<AdvisorReview> reviews = reviewRepo.findByAdvisorIdOrderByCreatedAtDesc(advisorId);
        return Map.of(
                "advisorName", profile.getName(),
                "averageRating", profile.getAverageRating(),
                "totalReviews", profile.getTotalReviews(),
                "totalSessions", profile.getTotalSessions(),
                "reviews", reviews
        );
    }

    /** Advisor cancels a session — points are restored to customer. */
    @Transactional
    public AdvisorSession advisorCancelSession(UUID sessionId, String reason) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        AdvisorSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
        if (session.getStatus() == AdvisorSession.SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed session.");
        }
        session.setStatus(AdvisorSession.SessionStatus.CANCELLED_BY_ADVISOR);
        session.setCancelledAt(LocalDateTime.now());
        session.setCancellationReason(reason);
        sessionRepo.save(session);

        // Restore points to customer
        restorePoints(session.getCustomerId(), session.getPointsCharged());

        // Notify customer
        kafkaTemplate.send("notification.command.send", String.format(
                "{\"userId\":\"%s\",\"message\":\"Your advisor session was cancelled. %d points have been restored.\"}",
                session.getCustomerId(), session.getPointsCharged()));

        return session;
    }

    /** Advisor reschedules a session. */
    @Transactional
    public AdvisorSession rescheduleSession(UUID sessionId, LocalDateTime newTime) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        AdvisorSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
        session.setScheduledAt(newTime);
        session.setStatus(AdvisorSession.SessionStatus.RESCHEDULED);
        sessionRepo.save(session);

        kafkaTemplate.send("notification.command.send", String.format(
                "{\"userId\":\"%s\",\"message\":\"Your advisor session has been rescheduled to %s.\"}",
                session.getCustomerId(), newTime));
        return session;
    }

    /** Browse all active advisors. */
    public List<AdvisorProfile> getActiveAdvisors() {
        return profileRepo.findByActiveTrue();
    }

    // =========================================================================
    // CUSTOMER: Book, cancel, and review sessions
    // =========================================================================

    /**
     * Book a session — deducts points from customer via Kafka.
     * Session cost = advisor.pointsCost (default 1000).
     */
    @Transactional
    public AdvisorSession bookSession(@NonNull UUID customerId, @NonNull UUID advisorId, LocalDateTime scheduledAt) {
        AdvisorProfile advisor = profileRepo.findById(advisorId)
                .orElseThrow(() -> new IllegalArgumentException("Advisor not found."));

        int cost = advisor.getPointsCost();

        // Deduct points via Kafka event to rewards-service (Locked until approved/rejected)
        kafkaTemplate.send("gamification.events", String.format(
                "{\"userId\":\"%s\",\"pointsEarned\":%d,\"event\":\"SESSION_BOOKED_PENDING\"}",
                customerId, -cost));

        AdvisorSession session = new AdvisorSession();
        session.setAdvisorId(advisorId);
        session.setCustomerId(customerId);
        session.setPointsCharged(cost);
        session.setScheduledAt(scheduledAt);
        session.setStatus(AdvisorSession.SessionStatus.PENDING_APPROVAL);
        sessionRepo.save(session);

        advisor.setTotalSessions(advisor.getTotalSessions() + 1);
        profileRepo.save(advisor);

        log.info("Session request created: customer={} advisor={} cost={} pts", customerId, advisor.getName(), cost);

        kafkaTemplate.send("notification.command.send", String.format(
                "{\"userId\":\"%s\",\"message\":\"Session request sent to %s for %s. %d points held pending approval.\"}",
                customerId, advisor.getName(), scheduledAt, cost));

        return session;
    }

    /** Advisor approves a pending session. */
    @Transactional
    public AdvisorSession approveSession(UUID sessionId) {
        AdvisorSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
        if (session.getStatus() != AdvisorSession.SessionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending sessions can be approved.");
        }
        session.setStatus(AdvisorSession.SessionStatus.APPROVED);
        sessionRepo.save(session);

        kafkaTemplate.send("notification.command.send", String.format(
                "{\"userId\":\"%s\",\"message\":\"Your advisor session on %s has been APPROVED.\"}",
                session.getCustomerId(), session.getScheduledAt()));
        return session;
    }

    /** Advisor rejects a pending session — points are refunded. */
    @Transactional
    public AdvisorSession rejectSession(UUID sessionId, String reason) {
        AdvisorSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
        if (session.getStatus() != AdvisorSession.SessionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending sessions can be rejected.");
        }
        session.setStatus(AdvisorSession.SessionStatus.REJECTED);
        session.setCancellationReason(reason);
        sessionRepo.save(session);

        // Restore points
        restorePoints(session.getCustomerId(), session.getPointsCharged());

        kafkaTemplate.send("notification.command.send", String.format(
                "{\"userId\":\"%s\",\"message\":\"Your advisor session request was rejected. Reason: %s. Points refunded.\"}",
                session.getCustomerId(), reason));
        return session;
    }

    /**
     * Customer cancels a session — full point refund.
     */
    @Transactional
    public AdvisorSession customerCancelSession(UUID sessionId, String reason) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        AdvisorSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
        if (session.getStatus() == AdvisorSession.SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed session.");
        }
        session.setStatus(AdvisorSession.SessionStatus.CANCELLED_BY_CUSTOMER);
        session.setCancelledAt(LocalDateTime.now());
        session.setCancellationReason(reason);
        sessionRepo.save(session);

        // Restore points
        restorePoints(session.getCustomerId(), session.getPointsCharged());
        return session;
    }

    /**
     * Customer submits a review after a completed session.
     */
    @Transactional
    public AdvisorReview submitReview(UUID sessionId, UUID customerId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1-5.");
        if (reviewRepo.existsBySessionId(sessionId)) {
            throw new IllegalArgumentException("Review already submitted for this session.");
        }
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        AdvisorSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
        if (session.getStatus() != AdvisorSession.SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Can only review completed sessions.");
        }

        AdvisorReview review = new AdvisorReview();
        review.setSessionId(sessionId);
        review.setAdvisorId(session.getAdvisorId());
        review.setCustomerId(customerId);
        review.setRating(rating);
        review.setComment(comment);
        reviewRepo.save(review);

        // Update denormalized advisor rating
        AdvisorProfile advisor = profileRepo.findById(Objects.requireNonNull(session.getAdvisorId())).orElseThrow();
        Double avg = reviewRepo.getAverageRatingByAdvisorId(advisor.getId());
        advisor.setAverageRating(avg != null ? avg : 0.0);
        advisor.setTotalReviews(reviewRepo.countByAdvisorId(advisor.getId()));
        profileRepo.save(advisor);

        log.info("Review submitted: advisor={} rating={} by customer={}", advisor.getName(), rating, customerId);
        return review;
    }

    /** Get customer's booked sessions. */
    public List<AdvisorSession> getCustomerSessions(UUID customerId) {
        return sessionRepo.findByCustomerIdOrderByBookedAtDesc(customerId);
    }

    // =========================================================================
    // Internal: Point restoration via Kafka
    // =========================================================================

    private void restorePoints(UUID userId, int points) {
        kafkaTemplate.send("gamification.events", String.format(
                "{\"userId\":\"%s\",\"pointsEarned\":%d,\"event\":\"SESSION_CANCELLED_REFUND\"}",
                userId, points));
        log.info("Points restored: +{} for user {} (session cancellation refund)", points, userId);
    }
}
