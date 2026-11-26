package com.coforge.hsbcdma.revenuemargin.AI;

import com.coforge.hsbcdma.revenuemargin.repository.RevenueMarginTmRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AiInsightService {
    private final RevenueMarginTmRepository revenueMarginTmRepository;
    private final AiQueryPlanner aiQueryPlanner;
    private final AiLlmService aiLlmService;
    private final AiAnalysisService aiAnalysisService;
    public AiInsightService(
            RevenueMarginTmRepository revenueMarginTmRepository,
            AiQueryPlanner aiQueryPlanner,
            AiAnalysisService aiAnalysisService,
            AiLlmService aiLlmService
    ) {
        this.revenueMarginTmRepository = revenueMarginTmRepository;
        this.aiQueryPlanner = aiQueryPlanner;
        this.aiAnalysisService = aiAnalysisService;
        this.aiLlmService = aiLlmService;
    }
    public List<AiInsightDto> generateInsights() {

        long totalResources = revenueMarginTmRepository.count();

        long activeResources =
                revenueMarginTmRepository.countByResourceStatus("Active");
        BigDecimal totalFyRevenue =
                revenueMarginTmRepository.getTotalFyRevenue();
        BigDecimal totalFyCost =
                revenueMarginTmRepository.getTotalFyCost();

        BigDecimal totalFyMargin =
                revenueMarginTmRepository.getTotalFyMargin();

        BigDecimal averageRate =
                revenueMarginTmRepository.getAverageEffectiveRate();

        long indiaResources =
                revenueMarginTmRepository.countByCountry("INDIA");

        long tmResources =
                revenueMarginTmRepository.countByPricingModel("Time and material");
        long negativeMarginResources =
                revenueMarginTmRepository
                        .countByFy202627MarginLessThan(BigDecimal.ZERO);
        BigDecimal negativeMarginPct =
                BigDecimal.valueOf(negativeMarginResources)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                BigDecimal.valueOf(totalResources),
                                2,
                                java.math.RoundingMode.HALF_UP
                        );
        return List.of(
                new AiInsightDto("FY Revenue", totalFyRevenue.toPlainString()),
                new AiInsightDto("FY Cost", totalFyCost.toPlainString()),
                new AiInsightDto(
                        "Calculated Margin %",
                        totalFyRevenue.compareTo(BigDecimal.ZERO) == 0
                                ? "0"
                                : totalFyRevenue
                                .subtract(totalFyCost)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(totalFyRevenue, 2, java.math.RoundingMode.HALF_UP)
                                .toPlainString()
                ),
                new AiInsightDto("Average Rate", averageRate.toPlainString()),
                new AiInsightDto("Total Resources", String.valueOf(totalResources)),
                new AiInsightDto("Active Resources", String.valueOf(activeResources)),
                new AiInsightDto("India Resources", String.valueOf(indiaResources)),
                new AiInsightDto(
                        "Negative Margin Resources",
                        String.valueOf(negativeMarginResources)
                ),
                new AiInsightDto(
                        "Negative Margin %",
                        negativeMarginPct.toPlainString()
                ),
                new AiInsightDto("T&M Resources", String.valueOf(tmResources))
        );
    }
    public String answerQuestion(String question) {

        question = question.toLowerCase();

        if (question.contains("revenue")) {

            return "FY Revenue is "
                    + revenueMarginTmRepository
                    .getTotalFyRevenue();
        }

        if (question.contains("cost")) {

            return "FY Cost is "
                    + revenueMarginTmRepository
                    .getTotalFyCost();
        }
        if (question.contains("margin")) {

            BigDecimal revenue =
                    revenueMarginTmRepository.getTotalFyRevenue();

            BigDecimal cost =
                    revenueMarginTmRepository.getTotalFyCost();

            BigDecimal marginPct =
                    revenue.subtract(cost)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(
                                    revenue,
                                    2,
                                    java.math.RoundingMode.HALF_UP
                            );

            return "Current FY Margin is "
                    + marginPct
                    + "%";
        }
        if (question.contains("negative margin")) {

            return "Negative Margin Resources are "
                    + revenueMarginTmRepository
                    .countByFy202627MarginLessThan(
                            BigDecimal.ZERO
                    );
        }
        if (question.contains("active")) {

            return "Active Resources are "
                    + revenueMarginTmRepository
                    .countByResourceStatus("Active");
        }

        if (question.contains("india")) {

            return "India Resources are "
                    + revenueMarginTmRepository
                    .countByCountry("INDIA");
        }

        return "I do not understand the question yet.";
    }
    public AiQuestionResponse askQuestion(String question) {

        AiQueryPlan plan =
                aiLlmService.createQueryPlan(question);

        return new AiQuestionResponse(
                aiAnalysisService.execute(plan)
        );
    }
}