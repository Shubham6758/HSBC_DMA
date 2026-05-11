package com.coforge.hsbcdma.service.DemandService;


import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.AddDemandRRDraft;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRRDraftRepository;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandsDraftRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class JDDownloadService {

    private final ProfileRepository profileRepo;
    private final AddDemandRRDraftRepository rrDraftRepo;
    private final AddDemandsDraftRepository draftRepo;
    private final AddDemandRepository demandRepo;

    private final Path baseDir;

    public JDDownloadService(AddDemandRRDraftRepository rrDraftRepo,
                             AddDemandsDraftRepository draftRepo,
                             AddDemandRepository demandRepo,
                             ProfileRepository profileRepo,
                             @Value("${app.jd.storage-path}") String storagePath) {
        this.rrDraftRepo = rrDraftRepo;
        this.draftRepo = draftRepo;
        this.demandRepo = demandRepo;
        this.profileRepo = profileRepo;
        this.baseDir = Path.of(storagePath).toAbsolutePath().normalize();
    }

    // ---------------------------
    // Draft download by fileName
    // ---------------------------
    public ResponseEntity<Resource> downloadDraftJDByFileName(String fileName) {

        validateFileName(fileName);

        List<AddDemandRRDraft> matches = rrDraftRepo.findByFileName(fileName);
        if (matches.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Draft file not found");
        }
        if (matches.size() > 1) {
            throw new ResponseStatusException(CONFLICT,
                    "Duplicate draft filenames found. Use draftId+fileName or make filenames unique.");
        }

        AddDemandRRDraft rrDraft = matches.get(0);
        Long draftId = rrDraft.getDraftId().getId();

        Path filePath = resolveDraftPath(draftId, fileName);
        return streamFile(filePath, fileName);
    }

    // ---------------------------
    // Demand download by fileName
    // ---------------------------
    public ResponseEntity<Resource> downloadDemandJDByFileName(String fileName) {

        validateFileName(fileName);

        List<AddDemand> matches = demandRepo.findByFileName(fileName);
        if (matches.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Demand file not found");
        }
        if (matches.size() > 1) {
            throw new ResponseStatusException(CONFLICT,
                    "Duplicate demand filenames found. Use demandId+fileName or make filenames unique.");
        }

        AddDemand demand = matches.get(0);

        Long folderDemandId = (demand.getDemandId() != null) ? demand.getDemandId() : demand.getId();

        Path filePath = resolveDemandPath(folderDemandId, fileName);
        return streamFile(filePath, fileName);
    }


    public ResponseEntity<Resource> downloadProfileByFileName(String fileName) {

        validateFileName(fileName);

        List<Profile> matches = profileRepo.findByFileName(fileName);
        if (matches.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Profile file not found");
        }
        if (matches.size() > 1) {
            throw new ResponseStatusException(CONFLICT,
                    "Duplicate profile filenames found. Make filenames unique in DB to download only by filename.");
        }

        Profile profile = matches.get(0);
        Long profileId = profile.getId();

        Path filePath = resolveProfilePath(profileId, fileName);
        return streamFile(filePath, fileName);
    }


    private Path resolveProfilePath(Long profileId, String fileName) {
        Path p = baseDir.resolve("profiles")
                .resolve("profile-" + profileId)
                .resolve(fileName)
                .normalize();
        ensureInsideBaseDir(p);
        return p;
    }



    // ---------------------------
    // Path resolvers
    // ---------------------------
    private Path resolveDraftPath(Long draftId, String fileName) {
        Path p = baseDir.resolve("draft")
                .resolve("draft-" + draftId)
                .resolve(fileName)
                .normalize();
        ensureInsideBaseDir(p);
        return p;
    }

    private Path resolveDemandPath(Long demandIdForFolder, String fileName) {
        // <baseDir>/demands/demand-<demandId>/<fileName>
        Path p = baseDir.resolve("demands")
                .resolve("demand-" + demandIdForFolder)
                .resolve(fileName)
                .normalize();
        ensureInsideBaseDir(p);
        return p;
    }

    // ---------------------------
    // Stream file as attachment
    // ---------------------------
    private ResponseEntity<Resource> streamFile(Path filePath, String downloadName) {

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResponseStatusException(NOT_FOUND, "File not found on disk");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + downloadName + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Invalid file URL", e);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Download failed", e);
        }
    }

    // ---------------------------
    // Security + Validation
    // ---------------------------
    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "fileName is required");
        }
        // only allow safe chars
        if (!fileName.matches("^[a-zA-Z0-9._-]+$")) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid filename");
        }
    }

    private void ensureInsideBaseDir(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(baseDir)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid path");
        }
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getName();
    }
}
