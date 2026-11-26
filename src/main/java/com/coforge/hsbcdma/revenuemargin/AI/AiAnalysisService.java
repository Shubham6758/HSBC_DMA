package com.coforge.hsbcdma.revenuemargin.AI;

import com.coforge.hsbcdma.revenuemargin.entity.RevenueMarginTmEntity;
import com.coforge.hsbcdma.revenuemargin.repository.RevenueMarginTmRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class AiAnalysisService {
    private final RevenueMarginTmRepository revenueMarginTmRepository;
    private final AiDataService aiDataService;
    public AiAnalysisService(
            RevenueMarginTmRepository revenueMarginTmRepository,
            AiDataService aiDataService
    ) {
        this.revenueMarginTmRepository = revenueMarginTmRepository;
        this.aiDataService = aiDataService;
    }
    public String execute(AiQueryPlan plan) {
        List<RevenueMarginTmEntity> data =
                aiDataService.getAllData();
        if (data.isEmpty()) {
            return "No data available";
        }
        RevenueMarginTmEntity firstRow =
                data.get(0);
        if ("REVENUE".equals(plan.metric())) {

            BigDecimal totalRevenue = data.stream()
                    .map(RevenueMarginTmEntity::getFy202627Rev)
                    .filter(Objects::nonNull)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );

            BigDecimal totalCost = data.stream()
                    .map(RevenueMarginTmEntity::getFy202627Cost)
                    .filter(Objects::nonNull)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );

            BigDecimal marginPercent =
                    BigDecimal.ZERO;

            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {

                marginPercent =
                        totalRevenue
                                .subtract(totalCost)
                                .multiply(
                                        BigDecimal.valueOf(100)
                                )
                                .divide(
                                        totalRevenue,
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }

            return """
            FY Revenue : %s

            FY Cost : %s

            FY Margin %% : %s
            """
                    .formatted(
                            totalRevenue,
                            totalCost,
                            marginPercent
                    );
        }
        if ("MARGIN".equals(plan.metric())) {

            BigDecimal totalRevenue = data.stream()
                    .map(RevenueMarginTmEntity::getFy202627Rev)
                    .filter(Objects::nonNull)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );

            BigDecimal totalCost = data.stream()
                    .map(RevenueMarginTmEntity::getFy202627Cost)
                    .filter(Objects::nonNull)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );

            BigDecimal marginPercent =
                    BigDecimal.ZERO;

            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {

                marginPercent =
                        totalRevenue
                                .subtract(totalCost)
                                .multiply(
                                        BigDecimal.valueOf(100)
                                )
                                .divide(
                                        totalRevenue,
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }

            return "FY Margin % = "
                    + marginPercent;
        }
        if ("GET".equals(plan.intent())
                && "REVENUE".equals(plan.metric())) {

            return """
Revenue Query

Intent  : %s
Metric  : %s
Period1 : %s
"""
                    .formatted(
                            plan.intent(),
                            plan.metric(),
                            plan.period1()
                    );
        }
        return plan.toString();
    }
    public AiQuestionResponse ask(String question) {

        String q = question.toLowerCase();

        if (q.contains("revenue")) {

            return new AiQuestionResponse(
                    execute(
                            new AiQueryPlan(
                                    "GET",
                                    "REVENUE",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    )
            );
        }

        if (q.contains("margin")) {

            return new AiQuestionResponse(
                    execute(
                            new AiQueryPlan(
                                    "GET",
                                    "MARGIN",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    )
            );
        }

        return new AiQuestionResponse(
                "I could not understand the question"
        );
    }
}