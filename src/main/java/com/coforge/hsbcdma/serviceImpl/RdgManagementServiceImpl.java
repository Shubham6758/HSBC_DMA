package com.coforge.hsbcdma.serviceImpl;

import com.coforge.hsbcdma.dto.RdgManagementRequestDto;
import com.coforge.hsbcdma.dto.RdgManagementResponseDto;
import com.coforge.hsbcdma.entity.RdgManagement;
import com.coforge.hsbcdma.exception.DuplicateAssignmentException;
import com.coforge.hsbcdma.exception.ResourceNotFoundException;
import com.coforge.hsbcdma.mapper.EntityDtoMapper;
import com.coforge.hsbcdma.repository.RdgManagementRepository;
import com.coforge.hsbcdma.service.RdgManagementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
public class RdgManagementServiceImpl implements RdgManagementService {

    private final RdgManagementRepository rdgManagementRepository;
    private final Path storageRoot;

    public RdgManagementServiceImpl(RdgManagementRepository rdgManagementRepository,
                                    @Value("${storage.rdg.root}") String storageRootCfg) {
        this.rdgManagementRepository = rdgManagementRepository;
        this.storageRoot = Paths.get(storageRootCfg.replace("\\", "/")).toAbsolutePath();
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory: " + this.storageRoot, e);
        }
    }

    @Override
    public void create(MultipartFile[] files, List<RdgManagementRequestDto> dtos) {
        if (files == null || files.length==0) {
            throw new IllegalArgumentException("Uploaded file is empty or missing");
        }
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Dtos is empty or missing");
        }

        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create storage directory: " + storageRoot.toAbsolutePath(), e);
        }

        if(files.length!= dtos.size()){
            throw new IllegalArgumentException("Files count must match with dtos count");
        }

        for(int i = 0; i < dtos.size(); i++){

            RdgManagementRequestDto dto =dtos.get(i);
            MultipartFile file = files[i];

            if (rdgManagementRepository.existsByDemandIdAndEmployeeId(dto.getDemandId(), dto.getEmployeeId())) {
                throw new DuplicateAssignmentException(
                        "Employee " + dto.getEmployeeId() +
                                " is already assigned to demand " + dto.getDemandId());
            }

            String fileName = saveFile(file);

            RdgManagement entity = new RdgManagement();
            BeanUtils.copyProperties(dto, entity);
            entity.setCvFileName(fileName);

            try {
                rdgManagementRepository.save(entity);
            } catch (DataIntegrityViolationException ex) {
                // Fallback in case of race conditions hitting the unique constraint
                throw new DuplicateAssignmentException(
                        "Employee " + dto.getEmployeeId() +
                                " is already assigned to demand " + dto.getDemandId(), ex);
            }
        }

    }


    @Override
    public List<RdgManagementResponseDto> getDataByDemandId(String demandId) {

        List<RdgManagement> list = rdgManagementRepository.findByDemandId(demandId);

        if(list.isEmpty()){
            throw new ResourceNotFoundException("No record found for demandId "+ demandId);
        }

        return list.stream().map(entity -> {

            Path filePath = storageRoot.resolve(entity.getCvFileName());

            try{

                byte [] fileBytes = Files.readAllBytes(filePath);
                String base64 = Base64.getEncoder().encodeToString(fileBytes);

                return  EntityDtoMapper.toRdgManagementDTO(entity,base64);

            }catch(IOException e){
                throw new RuntimeException("Error reading file: " + entity.getCvFileName() ,e);
            }

        }).toList();
    }

    @Override
    @Transactional
    public void deleteByDemandId(String demandId) {

        if(! rdgManagementRepository.existsByDemandId(demandId)){
            throw new ResourceNotFoundException("Demand not found with DemandId : " + demandId);
        }
        rdgManagementRepository.deleteByDemandId(demandId);
    }

    @Override
    public List<RdgManagementResponseDto> getAll() {
        return rdgManagementRepository.findAll().stream()
                .map(entity -> EntityDtoMapper.toRdgManagementDTO(entity,null))
                .collect(Collectors.toList());
    }

    private String saveFile(MultipartFile file) {
        // Keep original extension; sanitize filename
        try{
            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uniqueName = uniqueNamePreserveExtension(original);

            Path target = storageRoot.resolve(uniqueName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving file to " + storageRoot.toAbsolutePath() + ": " + e.getMessage(), e);
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
