package com.coforge.hsbcdma.chatbot.Service;


import com.coforge.hsbcdma.chatbot.Chatbot.ChatResponseDTO;

public interface ChatService {

    ChatResponseDTO processMessage(String message);
}