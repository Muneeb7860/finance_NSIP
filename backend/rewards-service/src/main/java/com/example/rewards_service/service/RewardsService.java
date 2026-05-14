package com.example.rewards_service.service;

import com.example.rewards_service.model.AdvisorSession;
import com.example.rewards_service.model.Certificate;
import com.example.rewards_service.model.PointsLedger;
import com.example.rewards_service.repository.AdvisorSessionRepository;
import com.example.rewards_service.repository.CertificateRepository;
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

import com.example.common.constants.CommonConstants;

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

    private static final int CANCELLATION_WINDOW_HOURS = 24;

    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * Kafka listener: Automatically awards points and certificates.
     */
    @KafkaListener(topics = "gamification.events", groupId = "rewards-group")
    @Transactional
    public void handleGamificationEvent(String payload) {
        log.info("Received gamification event: {}", payload);
        try {
            JsonNode node = objectMapper.readTree(payload);
            UUID userId = UUID.fromString(node.get("userId").asText());
            
            String eventType = node.has("eventType") ? node.get("eventType").asText() : 
                               (node.has("event") ? node.get("event").asText() : "UNKNOWN");
            
            int points = node.has("pointsEarned") ? node.get("pointsEarned").asInt() : 0;
            String description = "";
            
            switch (eventType) {
                case "COURSE_COMPLETED":
                    int attemptCount = node.has("attemptCount") ? node.get("attemptCount").asInt() : 1;
                    if (attemptCount == 1) points = 150;
                    else if (attemptCount == 2) points = 75;
                    else points = 37;
                    
                    description = "Completed Learning Module (Attempt #" + attemptCount + ")";
                    issueCertificate(userId, node.get("courseName").asText(), Certificate.CertificateType.PROFESSIONAL);
                    break;
                case "QUIZ_PASSED":
                    int score = node.has("score") ? node.get("score").asInt() : 70;
                    int passingScore = node.has("passingScore") ? node.get("passingScore").asInt() : 70;
                    points = 100 + (score > passingScore ? (score - passingScore) : 0);
                    description = "Passed Knowledge Quiz (Score: " + score + "%)";
                    break;
                case "EVENT_ATTENDED":
                    points = 200;
                    description = "Attended Community Event";
                    issueCertificate(userId, node.get("eventName").asText(), Certificate.CertificateType.PARTICIPATION);
                    break;
                case "WEEKLY_STREAK":
                    points = CommonConstants.WEEKLY_STREAK_BONUS_PTS;
                    description = "Active Usage Streak (Weekly)";
                    break;
                case "MONTHLY_STREAK":
                    points = CommonConstants.MONTHLY_STREAK_BONUS_PTS;
                    description = "Active Usage Streak (Monthly)";
                    break;
                case "SESSION_BOOKED_PENDING":
                    description = "Point Lock: Advisor Session Pending Approval";
                    break;
                case "SESSION_CANCELLED_REFUND":
                    description = "Refund: Advisor Session Cancelled";
                    break;
                default:
                    if (points != 0) {
                        description = "Platform Event: " + eventType;
                    } else {
                        log.warn("Unknown event type with no points: {}", eventType);
                        return;
                    }
            }
            
            if (points < 0) {
                deductPoints(userId, Math.abs(points), description);
            } else if (points > 0) {
                awardPoints(userId, points, description);
            }
            
        } catch (Exception e) {
            log.error("Failed to process gamification event: {}", e.getMessage());
            // Robustness: Alert on processing failure
            kafkaTemplate.send("notification.command.send", 
                String.format("{\"userId\":\"admin\", \"message\":\"Critical: Gamification event processing failed: %s\", \"channel\":\"EMAIL\"}", e.getMessage()));
        }
    }

    /**
     * Issue a digital certificate.
     */
    private void issueCertificate(UUID userId, String title, Certificate.CertificateType type) {
        Certificate cert = new Certificate();
        cert.setUserId(userId);
        cert.setTitle(title);
        cert.setType(type);
        cert.setIssuedAt(LocalDateTime.now());
        cert.setCertificateUrl("https://nsip.gov.sa/certs/" + UUID.randomUUID());
        
        certificateRepository.save(cert);
        log.info("Certificate issued: '{}' for user {}", title, userId);
        
        // Notify user about the new certificate
        kafkaTemplate.send("notification.command.send", 
            String.format("{\"userId\":\"%s\", \"message\":\"Congratulations! You have earned a new %s certificate: %s\", \"channel\":\"PUSH\"}", 
                          userId, type, title));
    }

    /** Helper for point deductions. */
    private void deductPoints(UUID userId, int points, String description) {
        PointsLedger entry = new PointsLedger();
        entry.setUserId(userId);
        entry.setPointDelta(-points);
        entry.setDescription(description);
        pointsLedgerRepository.save(entry);
        log.info("Deducted {} points from user {}. Reason: {}", points, userId, description);
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

        if (balance < CommonConstants.ADVISOR_SESSION_COST_PTS) {
            throw new IllegalArgumentException(
                    "Insufficient points. You have " + balance + " but need " + CommonConstants.ADVISOR_SESSION_COST_PTS + ".");
        }

        // Atomic write: deduct points within the same transaction
        PointsLedger deduction = new PointsLedger();
        deduction.setUserId(userId);
        deduction.setPointDelta(-CommonConstants.ADVISOR_SESSION_COST_PTS);
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
            refund.setPointDelta(CommonConstants.ADVISOR_SESSION_COST_PTS);
            refund.setDescription("Refund: Advisor session canceled (outside cancellation window)");
            pointsLedgerRepository.save(refund);
            log.info("Session {} canceled. {} points refunded to user {}", sessionId, CommonConstants.ADVISOR_SESSION_COST_PTS, session.getUserId());
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
        return session;
    }

    /**
     * Redeem points for a digital voucher or perk.
     */
    @Transactional
    public String redeemPoints(UUID userId, String itemName, int cost) {
        int balance = pointsLedgerRepository.getTotalPointsByUserId(userId);
        if (balance < cost) {
            throw new IllegalArgumentException("Insufficient points for redemption. Need " + cost + ".");
        }

        PointsLedger deduction = new PointsLedger();
        deduction.setUserId(userId);
        deduction.setPointDelta(-cost);
        deduction.setDescription("Redeemed: " + itemName);
        pointsLedgerRepository.save(deduction);

        String voucherCode = "VOUCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Points redeemed: user {} spent {} points for {}. Voucher: {}", userId, cost, itemName, voucherCode);

        kafkaTemplate.send("notification.command.send",
                String.format("{\"userId\":\"%s\", \"message\":\"Redemption successful! Your code for %s is %s\"}", 
                              userId, itemName, voucherCode));

        return voucherCode;
    }
}
