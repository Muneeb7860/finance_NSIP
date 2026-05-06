package com.example.claim_service.infrastructure.adapter.out.ai;

import com.example.claim_service.application.port.out.ClaimReviewerPort;
import com.example.claim_service.domain.model.Claim;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeminiClaimReviewer implements ClaimReviewerPort {

    @Value("${google.cloud.project-id:nsip-prod}")
    private String projectId;

    @Value("${google.cloud.location:us-central1}")
    private String location;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String[] review(Claim claim) {
        log.info("Requesting Gemini 1.5 Flash review for claim: {}", claim.getId());
        
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel("gemini-1.5-flash-001", vertexAI);
            
            String prompt = String.format(
                "Review this social insurance claim for potential fraud or eligibility issues. " +
                "User ID: %s, Type: %s, Amount: %s, Description: %s. " +
                "Provide a JSON response with 'status' (APPROVE, REJECT, or FLAG) and 'reasoning'.",
                claim.getUserId(), claim.getType(), claim.getAmount(), claim.getDescription()
            );

            GenerateContentResponse response = model.generateContent(prompt);
            String text = ResponseHandler.getText(response);
            
            // Extract JSON from response (handling potential markdown formatting)
            String jsonPart = text.contains("```json") 
                ? text.substring(text.indexOf("```json") + 7, text.lastIndexOf("```"))
                : text;

            JsonNode node = objectMapper.readTree(jsonPart);
            return new String[]{
                node.get("status").asText(),
                node.get("reasoning").asText()
            };
            
        } catch (Exception e) {
            log.error("Gemini AI review failed: {}", e.getMessage());
            return new String[]{"FLAG", "AI review unavailable: " + e.getMessage()};
        }
    }
}
