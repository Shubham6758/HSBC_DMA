package com.coforge.hsbcdma.serviceImpl;

import com.coforge.hsbcdma.dto.TaManagementRequestDto;
import com.coforge.hsbcdma.dto.TaManagementResponseDto;
import com.coforge.hsbcdma.entity.TaManagement;
import com.coforge.hsbcdma.exception.ResourceNotFoundException;
import com.coforge.hsbcdma.mapper.EntityDtoMapper;
import com.coforge.hsbcdma.repository.TaManagementRepository;
import com.coforge.hsbcdma.service.TaManagementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaManagementServiceImpl implements TaManagementService {

    private final TaManagementRepository taManagementRepository;
    private final Path storageRoot;
    private final Path cvRoot;
    private final Path irsRoot;


    public TaManagementServiceImpl(TaManagementRepository taManagementRepository,
                                   @Value("${storage.ta.root}") String storageRootCfg) {
        this.taManagementRepository = taManagementRepository;
        this.storageRoot = Paths.get(storageRootCfg.replace("\\", "/")).toAbsolutePath().normalize();
        this.cvRoot = storageRoot.resolve("cv");
        this.irsRoot = storageRoot.resolve("irs");

        try {
            Files.createDirectories(cvRoot);
            Files.createDirectories(irsRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory: " + this.storageRoot, e);
        }
    }

    @Override
    public void create(MultipartFile[] cvFiles, MultipartFile[] irsFiles, List<TaManagementRequestDto> dtos) {

        if (cvFiles == null || irsFiles == null || cvFiles.length==0 || irsFiles.length==0) {
            throw new IllegalArgumentException("CV / IRS file is empty or missing");
        }

        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Dtos is empty or missing");
        }

        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create storage directory: " + storageRoot.toAbsolutePath(), e);
        }

        if(cvFiles.length != dtos.size() || irsFiles.length!= dtos.size()){
            throw new IllegalArgumentException("Files count must match with dtos count");
        }

        for(int i = 0; i < dtos.size(); i++) {

            TaManagementRequestDto dto = dtos.get(i);
            MultipartFile cvFile = cvFiles[i];
            MultipartFile irsFile = irsFiles[i];

            String cvFileName = saveFile(cvFile,cvRoot);
            String irsFileName = saveFile(irsFile,irsRoot);

            TaManagement entity = new TaManagement();
            BeanUtils.copyProperties(dto, entity);
            entity.setCvFileName(cvFileName);
            entity.setIrsFileName(irsFileName);

            taManagementRepository.save(entity);
        }

    }


    @Override
    public List<TaManagementResponseDto> getDataByDemandId(String demandId) {

        List<TaManagement> list = taManagementRepository.findByDemandId(demandId);

        if(list.isEmpty()){
            throw new ResourceNotFoundException("No record found for demandId "+ demandId);
        }

        return list.stream().map(entity -> {

            Path cvFilePath = cvRoot.resolve(entity.getCvFileName());
            Path irsFilePath = irsRoot.resolve(entity.getIrsFileName());


            try{

                byte [] cvFileBytes = Files.readAllBytes(cvFilePath);
                byte [] irsFileBytes = Files.readAllBytes(irsFilePath);
                String cvBase64 = Base64.getEncoder().encodeToString(cvFileBytes);
                String irsBase64 = Base64.getEncoder().encodeToString(cvFileBytes);

                return EntityDtoMapper.toTaManagementDTO(entity,cvBase64,irsBase64);

            }catch(IOException e){
                throw new RuntimeException("Error reading file: " + entity.getIrsFileName() ,e);
            }

        }).toList();
    }

    @Override
    public void deleteByDemandId(String demandId) {

        if(! taManagementRepository.existsByDemandId(demandId)){
            throw new ResourceNotFoundException("Demand not found with DemandId : " + demandId);
        }
        taManagementRepository.deleteByDemandId(demandId);
    }

    @Override
    public List<TaManagementResponseDto> getAll() {
        return taManagementRepository.findAll().stream()
                .map(entity -> EntityDtoMapper.toTaManagementDTO(entity,null,null))
                .collect(Collectors.toList());
    }

    private String saveFile(MultipartFile file,Path targetDir) {
        // Keep original extension; sanitize filename
        try{
            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uniqueName = uniqueNamePreserveExtension(original);

            Path target = targetDir.resolve(uniqueName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving file to " + file.getOriginalFilename() + ": " + e.getMessage(), e);
        }
    }

    private String uniqueNamePreserveExtension(String sanitizedOriginal) {
        String uuid = UUID.randomUUID().toString();
        int dot = sanitizedOriginal.lastIndexOf('.');
        if (dot > 0) {
            String name = sanitizedOriginal.substring(0, dot);
            String ext = sanitizedOriginal.substring(dot); // includes dot (e.g., ".pdf")
            return name + "_" + uuid + ext;
        }
        return sanitizedOriginal + "_" + uuid;
    }
}
