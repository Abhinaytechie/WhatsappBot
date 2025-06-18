package com.whatsappBot.demo.controller;

import com.whatsappBot.demo.entity.ChatMessage;
import com.whatsappBot.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WhatsappWebhookController {
    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            Map<String, Object> changes = (Map<String, Object>) ((List<Map<String, Object>>) entries.get(0).get("changes")).get(0);
            Map<String, Object> value = (Map<String, Object>) changes.get("value");

            List<Map<String, Object>> contacts = (List<Map<String, Object>>) value.get("contacts");
            String userId = contacts.get(0).get("wa_id").toString();

            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
            String message = ((Map<String, Object>) messages.get(0).get("text")).get("body").toString();
            System.out.println("🔔 Received WhatsApp message from " + userId + ": " + message);
            ChatMessage chatMessage=new ChatMessage(
                    userId,message,LocalDateTime.now().toString()
            );
            String response=chatService.getResponse(chatMessage);
            // Call your response logic or service here
            return ResponseEntity.ok("EVENT_RECEIVED");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }
    }


    @GetMapping
    public ResponseEntity<String> verifyWebhook(@RequestParam("hub.challenge") String challenge) {
        // For webhook verification from Meta
        return ResponseEntity.ok(challenge);
    }
}
