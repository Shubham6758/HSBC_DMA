package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.TaManagementRequestDto;
import com.coforge.hsbcdma.dto.TaManagementResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaManagementService {

    void create(MultipartFile[] cvFile, MultipartFile[] irsFile, List<TaManagementRequestDto> requestDto);

    List<TaManagementResponseDto> getDataByDemandId(String demandId);

    void deleteByDemandId(String demandId);

    List<TaManagementResponseDto> getAll();
}
