package com.example.common.constants;

public class CommonConstants {
    public static final String API_V1_PREFIX = "/api/v1";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    // Business Rules (BRD v2.0)
    public static final java.math.BigDecimal CONTRIBUTION_RATE = new java.math.BigDecimal("0.04");
    public static final java.math.BigDecimal LOAN_CAP_PERCENT = new java.math.BigDecimal("0.30");
    public static final java.math.BigDecimal LOAN_HARD_MAX_SAR = new java.math.BigDecimal("45000");
    public static final int VESTING_YEARS = 3;
    
    // Gamification Rules
    public static final int ADVISOR_SESSION_COST_PTS = 1000;
    public static final int WEEKLY_STREAK_BONUS_PTS = 100;
    public static final int MONTHLY_STREAK_BONUS_PTS = 500;
}
