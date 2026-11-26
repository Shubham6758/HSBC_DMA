package com.coforge.hsbcdma.chatbot.Service;

import java.util.Map;

public interface Chatbot_QueryService {

    long countByEntity(String entity, Map<String, Object> filters);
}