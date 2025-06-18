package com.whatsappBot.demo.controller;

import com.whatsappBot.demo.entity.ChatMessage;
import com.whatsappBot.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @PostMapping("/req")
    public ResponseEntity<String> chat(@RequestBody ChatMessage chatMessage){
        try{
            String response=chatService.getResponse(chatMessage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
