package com.coforge.hsbcdma.revenuemargin.AI;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiCopilotController {

    private final AiAnalysisService aiAnalysisService;

    public AiCopilotController(
            AiAnalysisService aiAnalysisService
    ) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping("/api/copilot/query")
    public AiQuestionResponse query(
            @RequestBody AiQuestionRequest request
    ) {

        return aiAnalysisService.ask(
                request.question()
        );
    }
}