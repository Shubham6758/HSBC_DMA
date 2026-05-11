package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.RdgManagementRequestDto;
import com.coforge.hsbcdma.dto.RdgManagementResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RdgManagementService {

    void create(MultipartFile[] file, List<RdgManagementRequestDto> requestDto);

    List<RdgManagementResponseDto> getDataByDemandId(String demandId);

    void deleteByDemandId(String demandId);

    List<RdgManagementResponseDto> getAll();
}
