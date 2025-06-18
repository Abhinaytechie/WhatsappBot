package com.whatsappBot.demo.service;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsappBot.demo.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Service
public class ChatService {
    private WebClient webClient;
    @Value("${gemini.api.url}")
    private String apiUrl;
    @Value("${gemini.api.key}")
    private String apikey;

    public ChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getResponse(ChatMessage chatMessage) {
        String prompt=getPrompt(chatMessage.getMessage());
        String response=processContent(prompt);
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection("chat-history").add(chatMessage);
        } catch (Exception e) {
            System.err.println("❌ Failed to save chat to Firestore: " + e.getMessage());
        }
        return response;
    }

    private String processContent(String prompt) {
        Map<String,Object> reqBody= Map.of("contents",new Object[]{
                Map.of("parts", new Object[]{
                        Map.of("text",prompt)
                })
        });
        String response=webClient.post()
                .uri(apiUrl+apikey)
                .header("Content-Type","application/json")
                .bodyValue(reqBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return extractText(response);
    }

    private String extractText(String response) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode= mapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private String getPrompt(String message) {
        return """
        You are a helpful and friendly WhatsApp bot that responds to user messages.
        The user has sent the following message:

        "%s"

        Respond in a structured format using bullet points or sections where appropriate.
        """.formatted(message);
    }
}
