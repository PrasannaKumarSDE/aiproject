package com.example.transcript_summarizer.Sevices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIService {

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.model:llama-3.1-8b-instant}")
    private String model;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String summarize(String transcript, String instruction) {
        try {
            String userInstruction = (instruction == null || instruction.isBlank())
                    ? "Summarize the transcript in clear bullet points, followed by action items and owners."
                    : instruction;

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system",
                    "content", "You are an expert meeting summarizer. Keep it concise, structured; use markdown with headings: Summary, Key Points, Action Items."));
            messages.add(Map.of("role", "user",
                    "content", "Instruction: " + userInstruction + "\n\nTranscript:\n" + transcript));

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", model);
            payload.put("messages", messages);
            payload.put("temperature", 0.2);
            payload.put("max_tokens", 1024);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> resp = rest.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (!resp.getStatusCode().is2xxSuccessful()) {
                return "AI error: " + resp.getStatusCode() + " - " + resp.getBody();
            }

            // Debug log
            System.out.println("Groq API raw response: " + resp.getBody());

            JsonNode root = mapper.readTree(resp.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode choice0 = choices.get(0);
                // OpenAI-style
                if (choice0.has("message") && choice0.path("message").has("content")) {
                    return choice0.path("message").path("content").asText().trim();
                }
                // Fallbacks
                if (choice0.has("content")) return choice0.path("content").asText().trim();
                if (choice0.has("text")) return choice0.path("text").asText().trim();
            }
            return "No summary generated.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to contact AI provider: " + e.getMessage();
        }
    }
}
