package com.example.review_service.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class HafidaService {

    public Map<String, Object> provideAdvice(String userId, String query, Map<String, Object> context) {
        Map<String, Object> response = new HashMap<>();
        
        // Mock Gemini Intelligence
        String advice = "Based on your current profile, ";
        List<String> suggestedActions = new ArrayList<>();

        if (query.toLowerCase().contains("loan") || query.toLowerCase().contains("borrow")) {
            advice += "you are eligible for a 30% loan cap of your vested contributions. I recommend keeping your loan below SAR 20,000 to maintain your long-term streak bonuses.";
            suggestedActions.add("View Loan Eligibility");
            suggestedActions.add("Calculate Repayment");
        } else if (query.toLowerCase().contains("points") || query.toLowerCase().contains("rewards")) {
            advice += "you have accumulated a significant amount of loyalty points. You can redeem them for national travel vouchers or wellness checkups.";
            suggestedActions.add("Browse Rewards");
            suggestedActions.add("Redeem for Saudia Airlines");
        } else if (query.toLowerCase().contains("health") || query.toLowerCase().contains("wellness")) {
            advice += "I noticed you haven't enrolled in our Chronic Care program yet. Early enrollment grants you a 500-point bonus and free home visits.";
            suggestedActions.add("Enroll in Chronic Care");
            suggestedActions.add("Schedule Health Checkup");
        } else {
            advice += "I am here to help you navigate the National Social Insurance Platform. How can I support your financial safety today?";
            suggestedActions.add("View Dashboard");
            suggestedActions.add("Learn about Benefits");
        }

        response.put("advice", advice);
        response.put("suggestedActions", suggestedActions);
        response.put("timestamp", new Date());
        response.put("status", "SUCCESS");
        
        return response;
    }

    public List<Map<String, Object>> getProactiveAdvice(String userId) {
        List<Map<String, Object>> proactiveAdvice = new ArrayList<>();

        // Logic to simulate proactive alerts
        Map<String, Object> alert1 = new HashMap<>();
        alert1.put("id", UUID.randomUUID().toString());
        alert1.put("title", "Maximized Your Savings?");
        alert1.put("message", "You've hit a 3-month contribution streak! You are now eligible for a 100-point 'Consistency Bonus'.");
        alert1.put("actionLabel", "Claim Bonus");
        alert1.put("type", "REWARDS");
        proactiveAdvice.add(alert1);

        Map<String, Object> alert2 = new HashMap<>();
        alert2.put("id", UUID.randomUUID().toString());
        alert2.put("title", "Health Alert");
        alert2.put("message", "New wellness webinars are available for your age group. Participating earns you 'Impact Points'.");
        alert2.put("actionLabel", "View Webinars");
        alert2.put("type", "WELLNESS");
        proactiveAdvice.add(alert2);

        return proactiveAdvice;
    }
}
