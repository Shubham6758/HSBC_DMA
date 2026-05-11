package com.coforge.hsbcdma.service.DashboardServices;

import com.coforge.hsbcdma.dto.DashboardDTO.DashboardCountDto;
import com.coforge.hsbcdma.dto.DashboardDTO.HbuSummaryDto;
import com.coforge.hsbcdma.dto.DashboardDTO.LobSummaryDto;
import com.coforge.hsbcdma.dto.DashboardDTO.PrioritySummaryDto;
import com.coforge.hsbcdma.repository.DashboardRepositories.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public List<LobSummaryDto> getLobSummary(LocalDate fromDate, LocalDate toDate) {

        List<Object[]> lobRaw = dashboardRepository.getLobSummaryRaw(fromDate, toDate);

        return lobRaw.stream()
                .map(row -> new LobSummaryDto(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }
    public List<HbuSummaryDto> getHbuSummary(LocalDate fromDate, LocalDate toDate) {

        List<Object[]> hbuRaw = dashboardRepository.getHbuSummaryRaw(fromDate, toDate);

        return hbuRaw.stream()
                .map(row -> new HbuSummaryDto(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }

    public PrioritySummaryDto getPrioritySummary(
            LocalDate fromDate,
            LocalDate toDate) {

        List<Object[]> results =
                dashboardRepository.getPriorityCounts(fromDate, toDate);

        Object[] row = results.get(0);

        return new PrioritySummaryDto(
                row[0] != null ? ((Number) row[0]).longValue() : 0L,
                row[1] != null ? ((Number) row[1]).longValue() : 0L,
                row[2] != null ? ((Number) row[2]).longValue() : 0L
        );
    }

    //Dashboard Count

    public DashboardCountDto getDashboardCounts(LocalDate fromDate,
                                                LocalDate toDate) {

        List<Object[]> results =
                dashboardRepository.getDashboardCounts(fromDate, toDate);

        if (results.isEmpty()) {
            return new DashboardCountDto(0L, 0L, 0L, 0L);
        }

        Object[] row = results.get(0);

        return new DashboardCountDto(
                row[0] != null ? ((Number) row[0]).longValue() : 0L,
                row[1] != null ? ((Number) row[1]).longValue() : 0L,
                row[2] != null ? ((Number) row[2]).longValue() : 0L,
                row[3] != null ? ((Number) row[3]).longValue() : 0L
        );
    }
}

