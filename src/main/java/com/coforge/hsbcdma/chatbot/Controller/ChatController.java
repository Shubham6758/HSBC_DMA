package com.coforge.hsbcdma.chatbot.Controller;


import com.coforge.hsbcdma.chatbot.Chatbot.ChatRequestDTO;
import com.coforge.hsbcdma.chatbot.Chatbot.ChatResponseDTO;
import com.coforge.hsbcdma.chatbot.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO request) {
        return chatService.processMessage(request.getMessage());
    }
}
