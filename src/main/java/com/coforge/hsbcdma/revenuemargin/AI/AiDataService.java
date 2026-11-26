package com.coforge.hsbcdma.revenuemargin.AI;

import com.coforge.hsbcdma.revenuemargin.entity.RevenueMarginTmEntity;
import com.coforge.hsbcdma.revenuemargin.repository.RevenueMarginTmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiDataService {

    private final RevenueMarginTmRepository revenueMarginTmRepository;

    public AiDataService(
            RevenueMarginTmRepository revenueMarginTmRepository
    ) {
        this.revenueMarginTmRepository = revenueMarginTmRepository;
    }
    public String execute(AiQueryPlan plan) {

        return """
            Intent=%s
            Metric=%s
            Entity=%s
            Dimension=%s
            Period1=%s
            Period2=%s
            Filter=%s
            """
                .formatted(
                        plan.intent(),
                        plan.metric(),
                        plan.entity(),
                        plan.dimension(),
                        plan.period1(),
                        plan.period2(),
                        plan.filter()
                );
    }
    public List<RevenueMarginTmEntity> getAllData() {

        return revenueMarginTmRepository.findAll();
    }
}