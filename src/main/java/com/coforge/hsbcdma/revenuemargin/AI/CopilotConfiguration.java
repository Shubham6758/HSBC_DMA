package com.coforge.hsbcdma.revenuemargin.AI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CopilotConfiguration {

    @Bean
    public CopilotClient copilotClient() {

        return prompt -> {
            throw new UnsupportedOperationException(
                    "Real Copilot integration pending"
            );
        };
    }
}