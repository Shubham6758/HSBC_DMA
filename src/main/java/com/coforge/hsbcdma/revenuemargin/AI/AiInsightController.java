package com.coforge.hsbcdma.revenuemargin.AI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AiInsightController {

    private final AiInsightService aiInsightService;

    public AiInsightController(AiInsightService aiInsightService) {
        this.aiInsightService = aiInsightService;
    }

    @GetMapping("/ai/generate")
    public List<AiInsightDto> generate() {
        return aiInsightService.generateInsights();
    }
    @PostMapping("/ai/ask")
    public AiQuestionResponse ask(
            @RequestBody AiQuestionRequest request
    ) {

        return aiInsightService.askQuestion(
                request.question()
        );
    }
}