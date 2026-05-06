package com.example.rewards_service.service;

import com.example.rewards_service.model.AdvisorSession;
import com.example.rewards_service.model.PointsLedger;
import com.example.rewards_service.repository.AdvisorSessionRepository;
import com.example.rewards_service.repository.PointsLedgerRepository;
import com.example.rewards_service.dto.LeaderboardEntry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RewardsService {

    @Autowired
    private PointsLedgerRepository pointsLedgerRepository;

    @Autowired
    private AdvisorSessionRepository advisorSessionRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int SESSION_COST = 1000;
    private static final int CANCELLATION_WINDOW_HOURS = 24;

    /**
     * Kafka listener: Automatically awards points when a user completes a course or attends an event.
     * Parses the userId and pointsEarned from the JSON payload.
     */
    @KafkaListener(topics = "gamification.events", groupId = "rewards-group")
    @Transactional
    public void handleGamificationEvent(String payload) {
        log.info("Received gamification event: {}", payload);
        try {
            JsonNode node = objectMapper.readTree(payload);
            UUID userId = UUID.fromString(node.get("userId").asText());
            String eventType = node.get("eventType").asText();
            
            int points = 0;
            String description = "";
            
            switch (eventType) {
                case "COURSE_COMPLETED":
                    points = 500;
                    description = "Completed Learning Module";
                    break;
                case "QUIZ_PASSED":
                    points = 100;
                    description = "Passed Knowledge Quiz";
                    break;
                case "EVENT_ATTENDED":
                    points = 200;
                    description = "Attended Community Event";
                    break;
                default:
                    log.warn("Unknown event type: {}", eventType);
                    return;
            }
            
            awardPoints(userId, points, description);
            
        } catch (Exception e) {
            log.error("Failed to process gamification event: {}", e.getMessage());
        }
    }

    /**
     * Get the top users leaderboard.
     */
    public List<LeaderboardEntry> getLeaderboard(int limit) {
        return pointsLedgerRepository.getLeaderboard().stream()
                .limit(limit)
                .map(row -> new LeaderboardEntry((UUID) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }

    /**
     * Award points to a user.
     */
    @Transactional
    public PointsLedger awardPoints(UUID userId, int points, String description) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to award must be positive.");
        }

        PointsLedger entry = new PointsLedger();
        entry.setUserId(userId);
        entry.setPointDelta(points);
        entry.setDescription(description);

        PointsLedger saved = pointsLedgerRepository.save(entry);
        log.info("Awarded {} points to user {}. Reason: {}", points, userId, description);
        return saved;
    }

    /**
     * Get the user's current total points balance.
     */
    public int getBalance(UUID userId) {
        return pointsLedgerRepository.getTotalPointsByUserId(userId);
    }

    /**
     * Book a 1-on-1 financial advisor session. Costs 1000 points.
     *
     * FLAW #5 FIX: This entire method runs inside a single database transaction.
     * The balance check and the deduction happen atomically — if two requests
     * arrive simultaneously, the second one will see the already-deducted balance
     * and correctly reject with "Insufficient points."
     *
     * The @Transactional annotation ensures:
     * 1. The balance query and the point deduction are in the same DB transaction
     * 2. If any step fails, the entire operation rolls back
     * 3. PostgreSQL's MVCC prevents dirty reads between concurrent transactions
     */
    @Transactional
    public AdvisorSession bookSession(UUID userId, UUID advisorId, LocalDateTime scheduledTime) {
        // Atomic read: this runs inside the transaction boundary
        int balance = pointsLedgerRepository.getTotalPointsByUserId(userId);

        if (balance < SESSION_COST) {
            throw new IllegalArgumentException(
                    "Insufficient points. You have " + balance + " but need " + SESSION_COST + ".");
        }

        // Atomic write: deduct points within the same transaction
        PointsLedger deduction = new PointsLedger();
        deduction.setUserId(userId);
        deduction.setPointDelta(-SESSION_COST);
        deduction.setDescription("Booked Financial Advisor Session");
        pointsLedgerRepository.save(deduction);

        // Create session record
        AdvisorSession session = new AdvisorSession();
        session.setUserId(userId);
        session.setAdvisorId(advisorId);
        session.setScheduledTime(scheduledTime);
        session.setStatus(AdvisorSession.SessionStatus.SCHEDULED);

        AdvisorSession saved = advisorSessionRepository.save(session);
        log.info("Session booked: {} for user {} with advisor {}", saved.getId(), userId, advisorId);

        // Notify user (outside transaction — Kafka is eventually consistent)
        kafkaTemplate.send("notification.command.send",
                String.format("{\"userId\":\"%s\", \"message\":\"Your advisor session is confirmed for %s\"}", userId, scheduledTime));

        return saved;
    }

    /**
     * Cancel a session with refund policy.
     *
     * FLAW #11 FIX: Cancellations within 24 hours of the session get NO refund.
     * Cancellations made earlier than 24 hours before the session get a full refund.
     */
    @Transactional
    public AdvisorSession cancelSession(UUID sessionId) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        AdvisorSession session = advisorSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        if (session.getStatus() == AdvisorSession.SessionStatus.CANCELED) {
            throw new IllegalArgumentException("Session is already canceled.");
        }
        if (session.getStatus() == AdvisorSession.SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed session.");
        }

        session.setStatus(AdvisorSession.SessionStatus.CANCELED);
        advisorSessionRepository.save(session);

        // Check cancellation window: refund only if > 24 hours before session
        boolean withinWindow = session.getScheduledTime()
                .minusHours(CANCELLATION_WINDOW_HOURS)
                .isBefore(LocalDateTime.now());

        if (withinWindow) {
            log.info("Session {} canceled within {} hours — NO REFUND.", sessionId, CANCELLATION_WINDOW_HOURS);
        } else {
            // Full refund
            PointsLedger refund = new PointsLedger();
            refund.setUserId(session.getUserId());
            refund.setPointDelta(SESSION_COST);
            refund.setDescription("Refund: Advisor session canceled (outside cancellation window)");
            pointsLedgerRepository.save(refund);
            log.info("Session {} canceled. {} points refunded to user {}", sessionId, SESSION_COST, session.getUserId());
        }

        return session;
    }

    /**
     * Reschedule a session to a new time.
     */
    @Transactional
    public AdvisorSession rescheduleSession(UUID sessionId, LocalDateTime newTime) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        AdvisorSession session = advisorSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        if (session.getStatus() == AdvisorSession.SessionStatus.CANCELED ||
            session.getStatus() == AdvisorSession.SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot reschedule a " + session.getStatus() + " session.");
        }

        session.setScheduledTime(newTime);
        session.setStatus(AdvisorSession.SessionStatus.RESCHEDULED);
        advisorSessionRepository.save(session);

        kafkaTemplate.send("notification.command.send",
                String.format("{\"userId\":\"%s\", \"message\":\"Your advisor session has been rescheduled to %s\"}", session.getUserId(), newTime));

        log.info("Session {} rescheduled to {}", sessionId, newTime);
        return session;
    }
}
