package com.coforge.hsbcdma.revenuemargin.AI;

import org.springframework.stereotype.Service;

@Service
public class CopilotLlmService implements AiLlmService {
    private final CopilotClient copilotClient;
    private final CopilotPromptBuilder promptBuilder;
    private final AiQueryPlanner aiQueryPlanner;

    public CopilotLlmService(
            CopilotPromptBuilder promptBuilder,
            CopilotClient copilotClient,
            AiQueryPlanner aiQueryPlanner
    ) {
        this.promptBuilder = promptBuilder;
        this.copilotClient = copilotClient;
        this.aiQueryPlanner = aiQueryPlanner;
    }

    @Override
    public AiQueryPlan createQueryPlan(String question) {

        String prompt =
                promptBuilder.buildPrompt(question);

        System.out.println(prompt);

        CopilotQueryPlanResponse response =
                copilotClient.prompt(prompt);

        System.out.println(response.intent());

        return new AiQueryPlan(
                response.intent(),
                response.metric(),
                response.entity(),
                response.dimension(),
                response.period1(),
                response.period2(),
                response.filter()
        );
    }
}