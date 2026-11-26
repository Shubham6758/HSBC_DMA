package com.coforge.hsbcdma.revenuemargin.AI;

public record AiQueryPlan(

        String intent,

        String metric,

        String entity,

        String dimension,

        String period1,

        String period2,

        String filter

) {
}