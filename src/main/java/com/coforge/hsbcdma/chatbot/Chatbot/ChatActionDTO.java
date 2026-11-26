package com.coforge.hsbcdma.chatbot.Chatbot;

import java.util.Map;

public class ChatActionDTO {

    private String action;   // NAVIGATE / QUERY
    private String page;     // profiles / demands
    private String entity;   // demands / profiles
    private String operation; // count / list
    private Map<String, Object> filters;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPage() { return page; }
    public void setPage(String page) { this.page = page; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }
}