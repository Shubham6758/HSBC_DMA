package com.coforge.hsbcdma.chatbot.serviceimpl;

import com.coforge.hsbcdma.chatbot.Chatbot.ChatActionDTO;
import com.coforge.hsbcdma.chatbot.Chatbot.ChatResponseDTO;
import com.coforge.hsbcdma.chatbot.ChatbotUtility.LlmClient;
import com.coforge.hsbcdma.chatbot.Service.ChatService;
import com.coforge.hsbcdma.chatbot.Service.Chatbot_QueryService;
import com.coforge.hsbcdma.dto.DemandsDTO.DemandsFilterRequest;
import com.coforge.hsbcdma.service.AddNewDemandService1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private Chatbot_QueryService chatbotQueryService;

    @Autowired
    private LlmClient llmClient;

    @Autowired
    private AddNewDemandService1 addNewDemandService1;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ChatResponseDTO processMessage(String message) {
        try {
            String json = llmClient.getStructuredResponse(message);
            System.out.println("LLM RAW JSON: " + json);

            ChatActionDTO action = objectMapper.readValue(json, ChatActionDTO.class);
            System.out.println("USER MESSAGE: " + message);
            System.out.println("STRUCTURED JSON: " + json);

            if ("NAVIGATE".equalsIgnoreCase(action.getAction())) {
                return handleNavigation(action);
            }

            if ("QUERY".equalsIgnoreCase(action.getAction())) {
                return handleQuery(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatResponseDTO res = new ChatResponseDTO();
        res.setType("TEXT");
        res.setMessage("Sorry, I could not understand your request.");
        return res;
    }

    private ChatResponseDTO handleNavigation(ChatActionDTO action) {
        ChatResponseDTO res = new ChatResponseDTO();
        res.setType("NAVIGATION");

        String page = action.getPage();

        if (page == null || page.isBlank()) {
            res.setType("TEXT");
            res.setMessage("Could not determine page to navigate.");
            return res;
        }

        if ("profiles".equalsIgnoreCase(page)) {
            res.setRoute("/profileSheet");
            res.setMessage("Opening profiles page...");

        } else if ("demands".equalsIgnoreCase(page)) {
            res.setRoute("/demandsheet1");
            res.setMessage("Opening demands page...");

        } else if ("reports".equalsIgnoreCase(page)) {
            res.setRoute("/Report");
            res.setMessage("Opening reports page...");

        } else if ("track".equalsIgnoreCase(page)) {
            res.setRoute("/ProfileTracker");
            res.setMessage("Opening tracker page...");

        } else if ("resources".equalsIgnoreCase(page) || "resource".equalsIgnoreCase(page)) {
            res.setRoute("/resource-data");
            res.setMessage("Opening resources page...");

        } else {
            res.setType("TEXT");
            res.setMessage("Page not supported: " + page);
            return res;
        }

        res.setFilters(action.getFilters());
        return res;
    }


    private ChatResponseDTO handleQuery(ChatActionDTO action) {
        ChatResponseDTO res = new ChatResponseDTO();
        res.setType("DATA");

        try {
            String entity = action.getEntity();
            String operation = action.getOperation();

            if (entity == null || entity.isBlank()) {
                res.setMessage("Could not determine which data to search.");
                return res;
            }

            if (operation == null || operation.isBlank()) {
                res.setMessage("Could not determine what operation to perform.");
                return res;
            }

            if ("count".equalsIgnoreCase(operation)) {
                long total = chatbotQueryService.countByEntity(entity, action.getFilters());

                res.setTotal(total);
                res.setFilters(action.getFilters());
                res.setMessage(buildCountMessage(entity, total, action.getFilters()));

                return res;
            }

            res.setMessage("This query operation is not supported yet: " + operation);
            res.setFilters(action.getFilters());
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            res.setMessage("Error fetching data.");
            res.setFilters(action.getFilters());
            return res;
        }
    }

    private String buildCountMessage(String entity, long total, java.util.Map<String, Object> filters) {
        String readableEntity = entity;

        if ("demands".equalsIgnoreCase(entity)) {
            readableEntity = "demands";
        } else if ("profiles".equalsIgnoreCase(entity)) {
            readableEntity = "profiles";
        } else if ("resources".equalsIgnoreCase(entity)) {
            readableEntity = "resources";
        } else if ("tracker".equalsIgnoreCase(entity)) {
            readableEntity = "tracker records";
        }

        StringBuilder message = new StringBuilder();

        message.append("There are ")
                .append(total)
                .append(" matching ")
                .append(readableEntity);

        if (filters != null && !filters.isEmpty()) {
            message.append(" for ");

            boolean first = true;

            for (java.util.Map.Entry<String, Object> entry : filters.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }

                if (!first) {
                    message.append(", ");
                }

                message.append(formatFilterName(entry.getKey()))
                        .append(" = ")
                        .append(entry.getValue());

                first = false;
            }
        }

        message.append(".");

        return message.toString();
    }

    private String formatFilterName(String key) {
        if (key == null) {
            return "";
        }

        if ("primarySkills".equalsIgnoreCase(key)) {
            return "skill";
        }

        if ("secondarySkills".equalsIgnoreCase(key)) {
            return "secondary skill";
        }

        if ("hiringManager".equalsIgnoreCase(key)) {
            return "hiring manager";
        }

        if ("candidateName".equalsIgnoreCase(key)) {
            return "candidate name";
        }

        if ("resourceName".equalsIgnoreCase(key)) {
            return "resource name";
        }

        if ("experienceMin".equalsIgnoreCase(key)) {
            return "minimum experience";
        }

        if ("experienceMax".equalsIgnoreCase(key)) {
            return "maximum experience";
        }

        if ("isActive".equalsIgnoreCase(key)) {
            return "active";
        }

        if ("replacementRequired".equalsIgnoreCase(key)) {
            return "replacement required";
        }

        if ("replacementGiven".equalsIgnoreCase(key)) {
            return "replacement given";
        }

        return key;
    }

    private DemandsFilterRequest buildDemandFilter(ChatActionDTO action) {
        DemandsFilterRequest filter = new DemandsFilterRequest();

        if (action.getFilters() == null) {
            return filter;
        }

        Object primarySkills = action.getFilters().get("primarySkills");
        if (primarySkills != null) {
            filter.setPrimarySkillNames(List.of(primarySkills.toString()));
        }

        Object hiringManager = action.getFilters().get("hiringManager");
        if (hiringManager != null) {
            filter.setHiringManagerNames(List.of(hiringManager.toString()));
        }

        Object lob = action.getFilters().get("lob");
        if (lob != null) {
            filter.setLobNames(List.of(lob.toString()));
        }

        Object status = action.getFilters().get("status");
        if (status != null) {
            filter.setStatusNames(List.of(status.toString()));
        }

        Object hbu = action.getFilters().get("hbu");
        if (hbu != null) {
            filter.setHbuNames(List.of(hbu.toString()));
        }

        return filter;
    }
}