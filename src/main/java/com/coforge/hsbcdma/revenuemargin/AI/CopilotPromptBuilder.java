package com.coforge.hsbcdma.revenuemargin.AI;

import org.springframework.stereotype.Component;

@Component
public class CopilotPromptBuilder {

    public String buildPrompt(String question) {

        return """
You are an expert Revenue Margin Analysis Agent.
Convert the user question into JSON.
Return ONLY valid JSON.
Schema:
{
  "intent": "",
  "metric": "",
  "entity": "",
  "dimension": "",
  "period1": "",
  "period2": "",
  "filter": ""
}
Examples:
Question:
What is April revenue?
Response:
{
  "intent":"GET",
  "metric":"REVENUE",
  "entity":null,
  "dimension":null,
  "period1":"APRIL",
  "period2":null,
  "filter":null
}
Question:
Why did revenue decrease from April to May?
Response:
{
  "intent":"ANALYZE",
  "metric":"REVENUE",
  "entity":null,
  "dimension":null,
  "period1":"APRIL",
  "period2":"MAY",
  "filter":null
}
Question:
Show negative margin resources.
Response:
{
  "intent":"LIST",
  "metric":"MARGIN",
  "entity":"RESOURCE",
  "dimension":null,
  "period1":null,
  "period2":null,
  "filter":"NEGATIVE"
}
User Question:
""" + question;
    }
}