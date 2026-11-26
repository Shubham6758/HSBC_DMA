package com.coforge.hsbcdma.revenuemargin.AI;

import org.springframework.stereotype.Component;

@Component
public class AiQueryPlanner {

    public AiQueryPlan createPlan(String question) {

        String intent = "UNKNOWN";

        String q = question.toLowerCase();
        String metric = null;
        String period1 = null;

        if (q.contains("april")) {
            period1 = "APRIL";
        }
        else if (q.contains("may")) {
            period1 = "MAY";
        }
        else if (q.contains("june")) {
            period1 = "JUNE";
        }
        else if (q.contains("july")) {
            period1 = "JULY";
        }
        else if (q.contains("august")) {
            period1 = "AUGUST";
        }
        else if (q.contains("september")) {
            period1 = "SEPTEMBER";
        }
        else if (q.contains("october")) {
            period1 = "OCTOBER";
        }
        else if (q.contains("november")) {
            period1 = "NOVEMBER";
        }
        else if (q.contains("december")) {
            period1 = "DECEMBER";
        }
        else if (q.contains("january")) {
            period1 = "JANUARY";
        }
        else if (q.contains("february")) {
            period1 = "FEBRUARY";
        }
        else if (q.contains("march")) {
            period1 = "MARCH";
        }
        else if (q.contains("q1")) {
            period1 = "Q1";
        }
        else if (q.contains("q2")) {
            period1 = "Q2";
        }
        else if (q.contains("q3")) {
            period1 = "Q3";
        }
        else if (q.contains("q4")) {
            period1 = "Q4";
        }
        else if (q.contains("fy")) {
            period1 = "FY";
        }
        if (q.contains("revenue")) {
            metric = "REVENUE";
        }
        else if (q.contains("cost")) {
            metric = "COST";
        }
        else if (q.contains("margin")) {
            metric = "MARGIN";
        }
        else if (q.contains("resource")) {
            metric = "RESOURCE";
        }
        if (q.startsWith("what")) {
            intent = "GET";
        }
        else if (q.startsWith("show")) {
            intent = "LIST";
        }
        else if (q.startsWith("why")) {
            intent = "ANALYZE";
        }
        else if (q.startsWith("compare")) {
            intent = "COMPARE";
        }
        else if (q.startsWith("how many")) {
            intent = "COUNT";
        }

        return new AiQueryPlan(
                intent,
                metric,
                null,
                null,
                period1,
                null,
                null
        );
    }
}